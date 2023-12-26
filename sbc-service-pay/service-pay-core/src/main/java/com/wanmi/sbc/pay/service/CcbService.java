package com.wanmi.sbc.pay.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PascalNameFilter;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.pay.api.request.*;
import com.wanmi.sbc.pay.api.response.CcbPayOrderResponse;
import com.wanmi.sbc.pay.api.response.CcbPayStatusQueryResponse;
import com.wanmi.sbc.pay.api.response.CcbRefundAdResponse;
import com.wanmi.sbc.pay.api.response.CcbRefundFreightResponse;
import com.wanmi.sbc.pay.bean.dto.PartRfndDo;
import com.wanmi.sbc.pay.bean.dto.PartRfndDoSubDo;
import com.wanmi.sbc.pay.bean.enums.CcbDelFlag;
import com.wanmi.sbc.pay.bean.enums.CcbSubOrderType;
import com.wanmi.sbc.pay.bean.enums.TradeStatus;
import com.wanmi.sbc.pay.bean.enums.TradeType;
import com.wanmi.sbc.pay.ccbpay.CcbConfig;
import com.wanmi.sbc.pay.ccbpay.CcbHttpUtil;
import com.wanmi.sbc.pay.ccbpay.CcbRSASignUtil;
import com.wanmi.sbc.pay.ccbpay.CcbSplicingUtil;
import com.wanmi.sbc.pay.model.root.*;
import com.wanmi.sbc.pay.mq.CcbPayProducerService;
import com.wanmi.sbc.pay.repository.*;
import com.wanmi.sbc.pay.unionpay.acp.sdk.HttpClient;
import com.wanmi.sbc.pay.utils.GeneratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/6/13 11:05
 */
@Slf4j
@Service
public class CcbService {


    @Autowired
    private CcbBusinessRepository ccbBusinessRepository;

    @Autowired
    private CcbRuleRepository ccbRuleRepository;

    @Autowired
    private CcbRuleDetailRepository ccbRuleDetailRepository;

    @Autowired
    private TradeRecordRepository recordRepository;

    @Autowired
    private CcbPayOrderRecordRepository ccbPayOrderRecordRepository;

    @Autowired
    private CcbPayRecordRepository ccbPayRecordRepository;

    @Autowired
    private CcbLogService ccbLogService;

    @Autowired
    private CcbConfig ccbConfig;

    @Autowired
    private CcbRefundRetryService ccbRefundRetryService;

    @Autowired
    private CcbRefundRetryRepository ccbRefundRetryRepository;

    @Autowired
    private CcbRefundRecordRepository ccbRefundRecordRepository;

    @Autowired
    private CcbPayProducerService ccbPayProducerService;

    // 建行地址
    private static final String CCB_URL = "https://marketpay.ccb.com";

    // 查询建行商户信息
    private static final String CCB_QUERY_BUSINESS_URL = "/online/direct/enquireMkMrchOrder";

    // 查询分账规则
    private static final String CCB_QUERY_RULE_URL = "/online/direct/accountingRulesList";

    // 下单
    private static final String CCB_ORDER_URL = "/online/direct/gatherPlaceorder";

    /**
     * 支付状态查询
     */
    private static final String CCB_PAY_QUERY_URL = "/online/direct/gatherEnquireOrder";

    // 退单
    private static final String CCB_REFUND_URL = "/online/direct/refundOrder";

    // 确认收货
    private static final String CCB_ARRIVAL_URL = "/online/direct/mergeNoticeArrival";

    // 在途订单确认
    private static final String CCB_ON_THE_WAY_CONFIRM_URL = "/online/direct/confirmOnTheWay";

    // 分账状态查询接口
    private static final String CCB_SUB_ACCOUNT_STATUS_URL = "/online/direct/subAccountEnquire";


    private static final String CCB_CONFIRM_RECEIPT = "/online/direct/mergeNoticeArrival";

    private static final String CCB_REFUND_QUERY = "/online/direct/enquireRefundOrder";


    private static final String WX_OPENID_URL = "https://api.weixin.qq.com/sns/jscode2session";


    public Boolean verifySign(String request) {
        //开始验签
        JSONObject jsonObject = JSONObject.parseObject(request);
        //验签原串
        String jsonString = CcbSplicingUtil.createSign(request);
        log.info("建行验签：输出的原串为:{}", jsonString);
        String signInf = jsonObject.getString("Sign_Inf");
        // PayGatewayConfig payGatewayConfig = payDataService.queryConfigByNameAndStoreId(PayGatewayEnum.CCB, -1L);
        String platPk = ccbConfig.getPlatPk();

        boolean result = CcbRSASignUtil.verifySign(platPk, jsonString, signInf);
        log.info("建行验签：验签结果:{}", result);
        return result;
    }

    public Boolean verifySign2(String oriString, String signInf) {
        String platPk = ccbConfig.getPlatPk();
        return CcbRSASignUtil.verifySign(platPk, oriString, signInf);
    }


    @Transactional(rollbackFor = Exception.class)
    public void businessNotify(String request) {
        JSONObject obj = JSONObject.parseObject(request);
        // 01新增 02删除 03修改
        String opr_tp = obj.getString("Opr_Tp");

        if (Objects.equals("01", opr_tp)) {
            // 新增
            String ittparty_stm_id = obj.getString("Ittparty_Stm_Id");
            String py_chnl_cd = obj.getString("Py_Chnl_Cd");
            // String ittparty_tms = obj.getString("Ittparty_Tms");
            String mkt_mrch_id = obj.getString("Mkt_Mrch_Id");
            String mkt_mrch_nm = obj.getString("Mkt_Mrch_Nm");
            String pos_no = obj.getString("Pos_No");
            String mrch_crdt_tp = obj.getString("Mrch_Crdt_Tp");
            String march_crdt_no = obj.getString("March_Crdt_No");
            String mrch_cnter_cd = obj.getString("Mrch_Cnter_Cd");
            String crdt_tp = obj.getString("Crdt_Tp");
            String ctcpsn_nm = obj.getString("Ctcpsn_Nm");
            String crdt_no = obj.getString("Crdt_No");
            String mblph_no = obj.getString("Mblph_No");
            String udf_id = obj.getString("Udf_Id");
            String clrg_acc_no = obj.getString("Clrg_Acc_No");

            CcbBusiness business = ccbBusinessRepository.findByMktMrchId(mkt_mrch_id);
            LocalDateTime now = LocalDateTime.now();
            if (Objects.isNull(business)) {
                business = new CcbBusiness();
            }
            business.setDelFlag(CcbDelFlag.NO);
            business.setCreateTime(now);
            business.setUpdateTime(now);
            business.setIttpartyStmId(ittparty_stm_id);
            business.setPyChnlCd(py_chnl_cd);
            business.setMktMrchId(mkt_mrch_id);
            business.setMktMrchNm(mkt_mrch_nm);
            business.setPosNo(pos_no);
            business.setMrchCrdtTp(mrch_crdt_tp);
            business.setMrchCrdtNo(march_crdt_no);
            business.setMrchCnterCd(mrch_cnter_cd);
            business.setCrdtTp(crdt_tp);
            business.setCtcpsnNm(ctcpsn_nm);
            business.setCrdtNo(crdt_no);
            business.setMblphNo(mblph_no);
            business.setUdfId(udf_id);
            business.setClrgAccNo(clrg_acc_no);
            ccbBusinessRepository.saveAndFlush(business);
        }

        if (Objects.equals("02", opr_tp)) {
            // 删除
            String mkt_mrch_id = obj.getString("Mkt_Mrch_Id");
            CcbBusiness business = ccbBusinessRepository.findByMktMrchIdAndDelFlag(mkt_mrch_id, DeleteFlag.NO);
            if (Objects.nonNull(business)) {
                business.setUpdateTime(LocalDateTime.now());
                business.setDelFlag(CcbDelFlag.YES);
                ccbBusinessRepository.saveAndFlush(business);
            }

        }

        if (Objects.equals("03", opr_tp)) {
            // 修改
            String ittparty_stm_id = obj.getString("Ittparty_Stm_Id");
            String py_chnl_cd = obj.getString("Py_Chnl_Cd");
            // String ittparty_tms = obj.getString("Ittparty_Tms");
            String mkt_mrch_id = obj.getString("Mkt_Mrch_Id");
            String mkt_mrch_nm = obj.getString("Mkt_Mrch_Nm");
            String pos_no = obj.getString("Pos_No");
            String mrch_crdt_tp = obj.getString("Mrch_Crdt_Tp");
            String march_crdt_no = obj.getString("March_Crdt_No");
            String mrch_cnter_cd = obj.getString("Mrch_Cnter_Cd");
            String crdt_tp = obj.getString("Crdt_Tp");
            String ctcpsn_nm = obj.getString("Ctcpsn_Nm");
            String crdt_no = obj.getString("Crdt_No");
            String mblph_no = obj.getString("Mblph_No");
            String clrg_acc_no = obj.getString("Clrg_Acc_No");

            LocalDateTime now = LocalDateTime.now();
            CcbBusiness business = ccbBusinessRepository.findByMktMrchIdAndDelFlag(mkt_mrch_id, DeleteFlag.NO);
            if (Objects.isNull(business)) {
                business = new CcbBusiness();
            }
            business.setDelFlag(CcbDelFlag.NO);
            business.setUpdateTime(now);
            business.setIttpartyStmId(ittparty_stm_id);
            business.setPyChnlCd(py_chnl_cd);
            business.setMktMrchId(mkt_mrch_id);
            business.setMktMrchNm(mkt_mrch_nm);
            business.setPosNo(pos_no);
            business.setMrchCrdtTp(mrch_crdt_tp);
            business.setMrchCrdtNo(march_crdt_no);
            business.setMrchCnterCd(mrch_cnter_cd);
            business.setCrdtTp(crdt_tp);
            business.setCtcpsnNm(ctcpsn_nm);
            business.setCrdtNo(crdt_no);
            business.setMblphNo(mblph_no);
            business.setClrgAccNo(clrg_acc_no);
            ccbBusinessRepository.saveAndFlush(business);
        }

    }

    /**
     * 公钥:MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkwJJ9JhLIuQQpXb44DMv3fTeS9xdBoPkYlcNoIwnbdU9eQJdVbGBxMjfF4nHDVkXIdykhgHQUUZB6MdpIjMJ9vPJIR3NfeyE1slkj9rP6p4FDegyMpjpYt1dno0PyeZOf18XVhNp940+8tUBtjTe+WPvuODKVUNE47ww/bjTRWu0XLoY9Oj6fNyY0fBzxYdXcaxYz+gUfVTS8KkpvDR29cgB/dCsvJKRANPNBzyxcQm5OwnaKeuvTgdbqSzsU+AHzqZGOMpOwLEDtJB5GGf6+99Lz7i7fyNlFZ/H3hGROQ87PdTfC8hoIseNJx6fmIBmS6HL1aMg3YPbGmakrnR01QIDAQAB
     * 私钥:MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCTAkn0mEsi5BCldvjgMy/d9N5L3F0Gg+RiVw2gjCdt1T15Al1VsYHEyN8XiccNWRch3KSGAdBRRkHox2kiMwn288khHc197ITWyWSP2s/qngUN6DIymOli3V2ejQ/J5k5/XxdWE2n3jT7y1QG2NN75Y++44MpVQ0TjvDD9uNNFa7Rcuhj06Pp83JjR8HPFh1dxrFjP6BR9VNLwqSm8NHb1yAH90Ky8kpEA080HPLFxCbk7Cdop669OB1upLOxT4AfOpkY4yk7AsQO0kHkYZ/r730vPuLt/I2UVn8feEZE5Dzs91N8LyGgix40nHp+YgGZLocvVoyDdg9saZqSudHTVAgMBAAECggEAPCZgmirB+3mzcfBW7m0jNb7eQc3dZjAK/029LqaJ73+jkmKsS8bAYQMLfPcLseN0+o7r5kTRtp93kkYdNzgI6Llg3yLxlId05ukqM04FamrTiJgsCdXVEHWgu6HeBu8rySPgSYDSB88MJZGQDLxfS01fpZacUvyeMEwtA3NfRnSmVWhjv7Y+FLlwnoO1+bk7b8XjAL2ycXKQwUzf+K3TpKV93ILVlbMomNx0XI8cSUPb4X3R+U4Xi9/tXxyvXJ7LRL3pa3Z6eegJBTKf35PNMoqOr3yazzgi6ztt6dYowEcJowfYOdo70ld1ulaT8duP1O2SO4vFjdli7Zejjj1dFQKBgQDaFJxVQu7Lp6Q4WMikBYdFtegKZxNrdbT13RFyGZTS3eNbp544cPtYnEwFu6aD/uq8GSVFKEkUBD92A5n6Sky1ByQMw+nPfHuQm2PhHPMeGmoH0Xp5TvORi9Ra3HW6Ucd6WdvpRYXsWWoiasrTYCPc58oOb6H9TV1hDmbCuxl+DwKBgQCskhQmMvmaQT93h4oe6xLpzuyib24zhALJXQzMQFI2bHLf4qdnIACFC2ZrZY8pevDlHl0H4gs7gShp8A7vnn764D4zhjvyiqGgTPgwqfBL+YJQZOXv+NgPdeon5z/Dexs/JcZPsxXMMdCXC3L3NKcGRx8v8OSEMNqP3Jp0ej6C2wKBgQDDL8lQooyaaJwZ9pT2ASC2qv5ZOTJvbrWdjNSNfAnwxbo1kYuSVsUP5SxBwVdjMqij3Bm4kTr/GGI4XTbWhuOq7w48AqllFLR/lCTMGh4bJmcaQkpNAvYLocxZ2pHgRku03XJvkdMw3EKbNVv93kGa+TamvxGMZk8ajEo7JjzFzwKBgCG/L1GJBmaz+8VULB5/ueMfJAdf4x0P47udBrOqoGkkLBK8MCBqka9y20SFR8nE0Pb9SRQRkEDCsTSGTWVuiUZmM8O/rFlx4smGQ8LAeM1irakrEgwrjZT4aiIDhuSjXmCq8m0DiG+2DiR7iK/vC/qeTesCIK35H8SiT1IX1cGhAoGBAJa3tAb7facdkQVmlMc68BMqMwMNTWMGJGedOBxJn/jqV2FWMsQypUZgs3bEXHd269Ogo6cswPagy1f6UD66XCR5TP+cmLMGB4mghZYJgwE3NSGRNUJn2Iqz7HbH84ftalq3eSxSDJFoJ5ucVKhdCjeFAX9cdf4rrjE9fBmwAXOb
     * <p>
     * 惠市宝公钥：MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkkLYmF0qaoMNh9iOUa7q0H07wCn/vT8J3FCU4Hc+iUAAE209ezl8XjiD7iKjJ4KO/770bMwYZR6gbo5CI8dkKIzVthTAn/X9mlBZpOMGOIa2gH5zvViLLxEH9fjidKpGIVeXGPvtTbzeIxDHVjtcEKHeprO60Kz40wya3PXF9VJXB9y2HT3T2JPe4LwxeF2+1/sKTBtPMwGEk63IWWSg8qmONKtnqcyCnCgdVp4vC7H5YJUxivpg0yMhZi/q8kVGR1+uzUBia+gp7jattVn2qzrs/tKRXem5vOUYwvF6kqjCjLE7P+tHh0kSRS1WGJBHE/Y8KtHCC4gYzcfkBvk83QIDAQAB
     */
    @Transactional(rollbackFor = Exception.class)
    public void businessQuery() {
        // PayGatewayConfig payGatewayConfig = payDataService.queryConfigByNameAndStoreId(PayGatewayEnum.CCB, -1L);
        String platPk = ccbConfig.getPlatPk();
        String ittpartyStmId = ccbConfig.getIttpartyStmId();
        String pyChnlCd = ccbConfig.getPyChnlCd();
        String mktId = ccbConfig.getMktId();
        // 发起方时间戳
        String Ittparty_Tms = DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmssSSS");
        //发起方流水号
        String Ittparty_Jrnl_No = System.currentTimeMillis() + "";

        //开始日期
        String Stdt = "20230601";
        //结束日期
        String Eddt = DateUtil.format(LocalDateTime.now(), "yyyyMMdd");
        //版本号
        String Vno = "3";

        JSONObject json = new JSONObject(true);
        //发起方渠道编号
        json.put("Ittparty_Stm_Id", ittpartyStmId);
        //发起方渠道代码
        json.put("Py_Chnl_Cd", pyChnlCd);
        //发起方时间戳
        json.put("Ittparty_Tms", Ittparty_Tms);
        //发起方流水号 不许重复
        json.put("Ittparty_Jrnl_No", Ittparty_Jrnl_No);
        //市场编号
        json.put("Mkt_Id", mktId);
        //开始日期
        json.put("Stdt", Stdt);
        //结束日期
        json.put("Eddt", Eddt);
        //版本号
        json.put("Vno", Vno);

        String jsonString = CcbSplicingUtil.createSign(json.toString());

        String signInf = CcbRSASignUtil.sign(ccbConfig.getPrivateRsa(), jsonString);
        json.put("Sign_Inf", signInf);

        log.info("==========请求建行查询商户信息，入参：{}", json);
        try {
            String result = CcbHttpUtil.doJsonPost(CCB_URL + CCB_QUERY_BUSINESS_URL, json.toString());
            ;
            log.info("==========请求建行查询商户信息，返回信息：{}", result);
            //开始验签
            JSONObject obj = JSONObject.parseObject(result);
            //验签原串
            jsonString = CcbSplicingUtil.createSign(result);
            signInf = obj.getString("Sign_Inf");
            boolean verifySign = CcbRSASignUtil.verifySign(platPk, jsonString, signInf);
            if (verifySign) {
                String svc_rsp_st = obj.getString("Svc_Rsp_St");
                if (Objects.equals("00", svc_rsp_st)) {
                    // 成功
                    JSONArray list1 = obj.getJSONArray("List1");
                    if (CollectionUtils.isNotEmpty(list1)) {
                        for (int i = 0; i < list1.size(); i++) {
                            JSONObject o = list1.getJSONObject(i);

                            // 商家银行账号
                            String clrg_acc_no = o.getString("Clrg_Acc_No");
                            // 证件号码
                            String crdt_no = o.getString("Crdt_No");
                            // 证件类型
                            String crdt_tp = o.getString("Crdt_Tp");
                            // 联系人名称
                            String ctcpsn_nm = o.getString("Ctcpsn_Nm");
                            // 删除标识 00正常
                            String del_idr = o.getString("Del_Idr");
                            // 手机号码
                            String mblph_no = o.getString("Mblph_No");
                            // 市场商家编号
                            String mkt_mrch_id = o.getString("Mkt_Mrch_Id");
                            // 市场商家名称
                            String mkt_mrch_nm = o.getString("Mkt_Mrch_Nm");
                            // 商家柜台代码
                            String mrch_cnter_cd = o.getString("Mrch_Cnter_Cd");
                            // 商家证件号码
                            String mrch_crdt_no = o.getString("Mrch_Crdt_No");
                            // 商家证件类型
                            String mrch_crdt_tp = o.getString("Mrch_Crdt_Tp");
                            // POS编号
                            String pos_no = o.getString("Pos_No");
                            // 商家自定义编号
                            String udf_id = o.getString("Udf_Id");
                            CcbBusiness business = ccbBusinessRepository.findByMktMrchId(mkt_mrch_id);
                            LocalDateTime now = LocalDateTime.now();
                            if (Objects.isNull(business)) {
                                business = new CcbBusiness();
                                business.setCreateTime(now);
                            }
                            business.setUpdateTime(now);
                            business.setClrgAccNo(clrg_acc_no);
                            business.setCrdtNo(crdt_no);
                            business.setCrdtTp(crdt_tp);
                            business.setCtcpsnNm(ctcpsn_nm);
                            CcbDelFlag delFlag = CcbDelFlag.NO;
                            if (Objects.equals("01", del_idr)) {
                                delFlag = CcbDelFlag.YES;
                            }
                            if (Objects.equals("02", del_idr)) {
                                delFlag = CcbDelFlag.STOP;
                            }
                            if (Objects.equals("03", del_idr)) {
                                delFlag = CcbDelFlag.WAIT_DEL;
                            }
                            business.setDelFlag(delFlag);
                            business.setMblphNo(mblph_no);
                            business.setMktMrchId(mkt_mrch_id);
                            business.setMktMrchNm(mkt_mrch_nm);
                            business.setMrchCnterCd(mrch_cnter_cd);
                            business.setMrchCrdtNo(mrch_crdt_no);
                            business.setMrchCrdtTp(mrch_crdt_tp);
                            business.setPosNo(pos_no);
                            business.setUdfId(udf_id);
                            ccbBusinessRepository.saveAndFlush(business);
                        }
                    }
                }
            } else {
                log.error("==========请求建行查询商户信息，验签失败!!!!!!!!!!!!!");
            }
        } catch (Exception e) {
            log.error("======请求建行查询商户信息错误", e);
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public void ruleNotify(String request) {
        JSONObject obj = JSONObject.parseObject(request);
        // 维护类型 操作类型：00（新增）、01（修改）
        String mnt_type = obj.getString("Mnt_Type");

        String mkt_id = obj.getString("Mkt_Id");
        String mkt_nm = obj.getString("Mkt_Nm");
        String clrg_rule_id = obj.getString("Clrg_Rule_Id");
        String rule_nm = obj.getString("Rule_Nm");
        String rule_dsc = obj.getString("Rule_Dsc");
        String sub_acc_cyc = obj.getString("Sub_Acc_Cyc");
        String clrg_dlay_dys = obj.getString("Clrg_Dlay_Dys");
        String clrg_mode = obj.getString("Clrg_Mode");
        String clrg_mtdcd = obj.getString("Clrg_Mtdcd");
        String efdt = obj.getString("Efdt");
        String expdt = obj.getString("Expdt");
        JSONArray memblist = obj.getJSONArray("Memblist");

        LocalDateTime now = LocalDateTime.now();
        if (Objects.equals("00", mnt_type)) {
            // 新增
            CcbRule rule = ccbRuleRepository.findByClrgRuleId(clrg_rule_id);
            if (Objects.isNull(rule)) {
                rule = new CcbRule();
                rule.setCreateTime(now);
                rule.setDelFlag(DeleteFlag.NO);
                rule.setClrgRuleId(clrg_rule_id);
            }
            rule.setUpdateTime(now);
            rule.setMktId(mkt_id);
            rule.setMktNm(mkt_nm);
            rule.setRuleNm(rule_nm);
            rule.setRuleDsc(rule_dsc);
            rule.setSubAccCyc(sub_acc_cyc);
            rule.setClrgDlayDys(clrg_dlay_dys);
            rule.setClrgMode(clrg_mode);
            rule.setClrgMtdcd(clrg_mtdcd);
            if (StringUtils.isNotBlank(efdt)) {
                rule.setEfdt(LocalDate.parse(efdt, DateTimeFormatter.ofPattern(DateUtil.FMT_TIME_5)).atStartOfDay());
            }
            if (StringUtils.isNotBlank(expdt)) {
                rule.setExpdt(LocalDate.parse(expdt, DateTimeFormatter.ofPattern(DateUtil.FMT_TIME_5)).atStartOfDay());
            }
            ccbRuleRepository.saveAndFlush(rule);
            Long ruleId = rule.getRuleId();
            ccbRuleDetailRepository.deleteByRuleId(ruleId);
            if (CollectionUtils.isNotEmpty(memblist)) {
                List<CcbRuleDetail> list = new ArrayList<>();
                for (int i = 0; i < memblist.size(); i++) {
                    JSONObject o = memblist.getJSONObject(i);
                    String seq_no = o.getString("Seq_No");
                    String clrg_mtdcd_d = o.getString("Clrg_Mtdcd");
                    String clrg_pctg = o.getString("Clrg_Pctg");
                    String amt = o.getString("Amt");
                    CcbRuleDetail detail = new CcbRuleDetail();
                    detail.setRuleId(ruleId);
                    if (StringUtils.isNotBlank(seq_no)) {
                        detail.setSeqNo(Integer.valueOf(seq_no));
                    }
                    detail.setClrgMtdcd(clrg_mtdcd_d);
                    detail.setClrgPctg(clrg_pctg);
                    if (StringUtils.isNotBlank(amt)) {
                        detail.setAmt(new BigDecimal(amt));
                    }
                    list.add(detail);
                }
                ccbRuleDetailRepository.saveAll(list);
            }

        }
        if (Objects.equals("01", mnt_type)) {
            // 修改
            CcbRule rule = ccbRuleRepository.findByClrgRuleId(clrg_rule_id);
            if (Objects.isNull(rule)) {
                rule = new CcbRule();
                rule.setCreateTime(now);
                rule.setDelFlag(DeleteFlag.NO);
                rule.setClrgRuleId(clrg_rule_id);
            }
            rule.setUpdateTime(now);
            rule.setMktId(mkt_id);
            rule.setMktNm(mkt_nm);
            rule.setRuleNm(rule_nm);
            rule.setRuleDsc(rule_dsc);
            rule.setSubAccCyc(sub_acc_cyc);
            rule.setClrgDlayDys(clrg_dlay_dys);
            rule.setClrgMode(clrg_mode);
            rule.setClrgMtdcd(clrg_mtdcd);
            if (StringUtils.isNotBlank(efdt)) {
                rule.setEfdt(LocalDate.parse(efdt, DateTimeFormatter.ofPattern(DateUtil.FMT_TIME_5)).atStartOfDay());
            }
            if (StringUtils.isNotBlank(expdt)) {
                rule.setExpdt(LocalDate.parse(expdt, DateTimeFormatter.ofPattern(DateUtil.FMT_TIME_5)).atStartOfDay());
            }
            ccbRuleRepository.saveAndFlush(rule);
            Long ruleId = rule.getRuleId();
            ccbRuleDetailRepository.deleteByRuleId(ruleId);
            if (CollectionUtils.isNotEmpty(memblist)) {
                List<CcbRuleDetail> list = new ArrayList<>();
                for (int i = 0; i < memblist.size(); i++) {
                    JSONObject o = memblist.getJSONObject(i);
                    String seq_no = o.getString("Seq_No");
                    String clrg_mtdcd_d = o.getString("Clrg_Mtdcd");
                    String clrg_pctg = o.getString("Clrg_Pctg");
                    String amt = o.getString("Amt");
                    CcbRuleDetail detail = new CcbRuleDetail();
                    detail.setRuleId(ruleId);
                    if (StringUtils.isNotBlank(seq_no)) {
                        detail.setSeqNo(Integer.valueOf(seq_no));
                    }
                    detail.setClrgMtdcd(clrg_mtdcd_d);
                    detail.setClrgPctg(clrg_pctg);
                    if (StringUtils.isNotBlank(amt)) {
                        detail.setAmt(new BigDecimal(amt));
                    }
                    list.add(detail);
                }
                ccbRuleDetailRepository.saveAll(list);
            }
        }
    }

    public void ruleQuery() {

        ccbRuleQuery( 1);
    }

    public void ccbRuleQuery(Integer page) {
        String platPk = ccbConfig.getPlatPk();
        String ittpartyStmId = ccbConfig.getIttpartyStmId();
        String pyChnlCd = ccbConfig.getPyChnlCd();
        String mktId = ccbConfig.getMktId();
        // 发起方时间戳
        String Ittparty_Tms = DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmssSSS");
        //发起方流水号
        String Ittparty_Jrnl_No = System.currentTimeMillis() + "";
        //清算规则编号
        String Clrg_Rule_Id = "";
        //规则名称
        String Rule_Nm = "";
        //每页条数
        String Rec_In_Page = "20";
        //当前跳转页
        // String Page_Jump = "0";
        //版本号
        String Vno = "3";

        JSONObject json = new JSONObject(true);
        //发起方渠道编号
        json.put("Ittparty_Stm_Id", ittpartyStmId);
        //发起方渠道代码
        json.put("Py_Chnl_Cd", pyChnlCd);
        //发起方时间戳
        json.put("Ittparty_Tms", Ittparty_Tms);
        //发起方流水号 不许重复
        json.put("Ittparty_Jrnl_No", Ittparty_Jrnl_No);
        //市场编号
        json.put("Mkt_Id", mktId);
        //清算规则编号
        //json.put("Clrg_Rule_Id", Clrg_Rule_Id);
        //规则名称
        //json.put("Rule_Nm", Rule_Nm);
        //每页条数
        json.put("Rec_In_Page", Rec_In_Page);
        //当前跳转页
        json.put("Page_Jump", String.valueOf(page));
        //版本号
        json.put("Vno", Vno);

        String jsonString = CcbSplicingUtil.createSign(json.toString());
        String signInf = CcbRSASignUtil.sign(ccbConfig.getPrivateRsa(), jsonString);
        json.put("Sign_Inf", signInf);

        log.info("==========请求建行查询分账规则信息，入参：{}", json);
        try {
            String result = CcbHttpUtil.doJsonPost(CCB_URL + CCB_QUERY_RULE_URL, json.toString());
            log.info("==========请求建行查询分账规则信息，返回信息：{}", result);
            //开始验签
            JSONObject obj = JSONObject.parseObject(result);
            //验签原串
            jsonString = CcbSplicingUtil.createSign(result);
            signInf = obj.getString("Sign_Inf");
            boolean verifySign = CcbRSASignUtil.verifySign(platPk, jsonString, signInf);
            if (verifySign) {
                String svc_rsp_st = obj.getString("Svc_Rsp_St");
                if (Objects.equals("00", svc_rsp_st)) {
                    JSONArray rulelist = obj.getJSONArray("Rulelist");

                    if (CollectionUtils.isNotEmpty(rulelist)) {
                        LocalDateTime now = LocalDateTime.now();
                        for (int i = 0; i < rulelist.size(); i++) {
                            JSONObject rule = rulelist.getJSONObject(i);
                            String clrg_dlay_dys = rule.getString("Clrg_Dlay_Dys");
                            String clrg_mode = rule.getString("Clrg_Mode");
                            String clrg_mtdcd = rule.getString("Clrg_Mtdcd");
                            String clrg_rule_id = rule.getString("Clrg_Rule_Id");
                            String efdt = rule.getString("Efdt");
                            String expdt = rule.getString("Expdt");
                            String mkt_id = rule.getString("Mkt_Id");
                            String mkt_nm = rule.getString("Mkt_Nm");
                            String rule_dsc = rule.getString("Rule_Dsc");
                            String rule_nm = rule.getString("Rule_Nm");
                            String sub_acc_cyc = rule.getString("Sub_Acc_Cyc");
                            JSONArray memblist = rule.getJSONArray("Memblist");

                            CcbRule ccbRule = ccbRuleRepository.findByClrgRuleId(clrg_rule_id);
                            if (Objects.isNull(ccbRule)) {
                                ccbRule = new CcbRule();
                                ccbRule.setCreateTime(now);
                                ccbRule.setDelFlag(DeleteFlag.NO);
                            }
                            ccbRule.setClrgRuleId(clrg_rule_id);
                            ccbRule.setUpdateTime(now);
                            ccbRule.setMktId(mkt_id);
                            ccbRule.setMktNm(mkt_nm);
                            ccbRule.setRuleNm(rule_nm);
                            ccbRule.setRuleDsc(rule_dsc);
                            ccbRule.setSubAccCyc(sub_acc_cyc);
                            ccbRule.setClrgDlayDys(clrg_dlay_dys);
                            ccbRule.setClrgMode(clrg_mode);
                            ccbRule.setClrgMtdcd(clrg_mtdcd);
                            if (StringUtils.isNotBlank(efdt)) {
                                ccbRule.setEfdt(LocalDate.parse(efdt, DateTimeFormatter.ofPattern(DateUtil.FMT_TIME_5)).atStartOfDay());
                            }
                            if (StringUtils.isNotBlank(expdt)) {
                                ccbRule.setExpdt(LocalDate.parse(expdt, DateTimeFormatter.ofPattern(DateUtil.FMT_TIME_5)).atStartOfDay());
                            }
                            ccbRuleRepository.saveAndFlush(ccbRule);
                            Long ruleId = ccbRule.getRuleId();
                            ccbRuleDetailRepository.deleteByRuleId(ruleId);
                            if (CollectionUtils.isNotEmpty(memblist)) {
                                List<CcbRuleDetail> list = new ArrayList<>();
                                for (int j = 0; j < memblist.size(); j++) {
                                    JSONObject o = memblist.getJSONObject(j);
                                    String seq_no = o.getString("Seq_No");
                                    String clrg_mtdcd_d = o.getString("Clrg_Mtdcd");
                                    String clrg_pctg = o.getString("Clrg_Pctg");
                                    String amt = o.getString("Amt");
                                    CcbRuleDetail detail = new CcbRuleDetail();
                                    detail.setRuleId(ruleId);
                                    if (StringUtils.isNotBlank(seq_no)) {
                                        detail.setSeqNo(Integer.valueOf(seq_no));
                                    }
                                    detail.setClrgMtdcd(clrg_mtdcd_d);
                                    detail.setClrgPctg(clrg_pctg);
                                    if (StringUtils.isNotBlank(amt)) {
                                        detail.setAmt(new BigDecimal(amt));
                                    }
                                    list.add(detail);
                                }
                                ccbRuleDetailRepository.saveAll(list);
                            }
                        }
                    }
                    Integer total_page = obj.getInteger("Total_Page");
                    if (Objects.nonNull(total_page) && total_page > page) {
                        page++;
                        ccbRuleQuery(page);
                    }
                }
            } else {
                log.error("==========请求建行查询分账规则信息，验签失败!!!!!!!!!!!!!");
            }
        } catch (Exception e) {
            log.error("==========请求建行查询分账规则信息错误", e);
        }
    }


    /**
     * 普通订单 聚合下单
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public CcbPayOrderResponse ccbPayOrder(CcbPayOrderRequest request) {

        PayTradeRecord record = recordRepository.findByBusinessId(request.getBusinessId());
        if (!Objects.isNull(record) && record.getStatus() == TradeStatus.SUCCEED) {
            //如果重复支付，判断状态，已成功状态则做异常提示
            throw new SbcRuntimeException("K-100203");
        }
        CcbLog ccbLog = new CcbLog();
        ccbLog.setBusinessId(request.getBusinessId());
        ccbLog.setRequestType(1);
        ccbLog.setCreateTime(LocalDateTime.now());
        try {

            JSONObject json = buildgGatherByMergePlaceorderJson(request);

            String jsonString = CcbSplicingUtil.createSign(json.toString());
            String signInf = CcbRSASignUtil.sign(ccbConfig.getPrivateRsa(), jsonString);
            json.put("Sign_Inf", signInf);

            ccbLog.setRequest(json.toJSONString());

            log.info("========建行支付聚合普通订单下单，入参：{}", json);

            String result = CcbHttpUtil.doJsonPost(CCB_URL + CCB_ORDER_URL, json.toString());
            log.info("========建行支付聚合普通订单下单，返回：{}", result);
            //开始验签
            JSONObject jsonObject = JSONObject.parseObject(result);

            //  保存日志
            ccbLog.setResponse(jsonObject.toJSONString());

            //验签原串
            jsonString = CcbSplicingUtil.createSign(result);
            signInf = jsonObject.getString("Sign_Inf");
            boolean verifySign = CcbRSASignUtil.verifySign(ccbConfig.getPlatPk(), jsonString, signInf);
            if (verifySign) {
                // 支付流水号
                String py_trn_no = jsonObject.getString("Py_Trn_No");
                // 惠市宝生成主订单号
                String prim_ordr_no = jsonObject.getString("Prim_Ordr_No");
                JSONArray orderlist = jsonObject.getJSONArray("Orderlist");
                LocalDateTime now = LocalDateTime.now();

                // save ccb_pay_record
                CcbPayRecord payRecord = new CcbPayRecord();
                payRecord.setBusinessId(request.getBusinessId());
                payRecord.setMktId(ccbConfig.getMktId());
                payRecord.setMainOrdrNo(request.getMainOrderNo());
                payRecord.setPrimOrdrNo(prim_ordr_no);
                payRecord.setPyTrnNo(py_trn_no);
                //
                String ccbPymdCd = getCcbPymdCd(request.getPayType());
                payRecord.setPymdCd(ccbPymdCd);
                payRecord.setOrdrTamt(request.getOrderAmount());
                payRecord.setTxnTamt(request.getTxnAmt());
                //
                payRecord.setClrgDt(request.getClrgDt());
                payRecord.setStatus(0);
                payRecord.setCreateTime(now);
                payRecord.setUpdateTime(now);
                payRecord.setPayType(request.getPayType());
                ccbPayRecordRepository.saveAndFlush(payRecord);

                if (CollectionUtils.isNotEmpty(orderlist)) {
                    Map<String, CcbSubPayOrderRequest> subPayOrderRequestMap = request.getSubOrderList().stream().collect(Collectors.toMap(CcbSubPayOrderRequest::getCmdtyOrderNo, Function.identity(), (o1, o2) -> o1));
                    List<CcbPayOrderRecord> orderList = new ArrayList<>();
                    for (int i = 0; i < orderlist.size(); i++) {
                        JSONObject obj = orderlist.getJSONObject(i);
                        String cmdty_ordr_no = obj.getString("Cmdty_Ordr_No");
                        String sub_ordr_id = obj.getString("Sub_Ordr_Id");
                        CcbSubPayOrderRequest subRequest = subPayOrderRequestMap.get(cmdty_ordr_no);
                        if (Objects.nonNull(subRequest)) {
                            CcbPayOrderRecord orderRecord = getCcbPayOrderRecord(request, py_trn_no, prim_ordr_no, now, sub_ordr_id, subRequest);
                            orderList.add(orderRecord);
                        }
                    }
                    ccbPayOrderRecordRepository.saveAll(orderList);
                }

                // record.setChargeId(prim_ordr_no);
                // record.setTradeNo(py_trn_no);
                // recordRepository.saveAndFlush(record);
                ccbPayProducerService.delayCcbConfirm(py_trn_no,ccbConfig.getPayStatusQueryTime() * 60L * 1000L);
                return JSON.parseObject(result, CcbPayOrderResponse.class);
            } else {
                log.error("========建行支付聚合普通订单下单，验签失败。");
                throw new SbcRuntimeException("K-000001");
            }
        } catch (Exception e) {
            log.error("========建行支付聚合普通订单下单，错误", e);
            throw new SbcRuntimeException("K-000001");
        } finally {
            ccbLogService.saveLog(ccbLog);
        }
    }

    private static String getCcbPymdCd(Integer payType) {
        String pymdCd = null;
        // 建行H5页面
        if (Objects.equals(3, payType)) {
            pymdCd = "03";
        }
        // 微信小程序
        if (Objects.equals(1, payType)) {
            pymdCd = "05";
        }
        // 支付宝小程序
        if (Objects.equals(2, payType)) {
            pymdCd = "14";
        }
        // 二维码：4 鲸币充值 5 订单 7 广告
        if (Objects.equals(4,payType)
                || Objects.equals(5, payType)
                || Objects.equals(7, payType)) {
            pymdCd = "07";
        }
        // 对公： 6 订单对公 8 鲸币充值对公
        if (Objects.equals(6, payType)
                || Objects.equals(8, payType)) {
            pymdCd = "01";
        }
        return StringUtils.isNotBlank(pymdCd) ? pymdCd : "03";
    }

    private static CcbPayOrderRecord getCcbPayOrderRecord(CcbPayOrderRequest request, String py_trn_no, String prim_ordr_no, LocalDateTime now, String sub_ordr_id, CcbSubPayOrderRequest subRequest) {
        CcbPayOrderRecord orderRecord = new CcbPayOrderRecord();
        orderRecord.setMktMrchtId(subRequest.getMktMrchtId());
        orderRecord.setBusinessId(subRequest.getTid());
        orderRecord.setOrderAmount(subRequest.getOrderAmount());
        orderRecord.setTxnAmt(subRequest.getTxnAmt());
        orderRecord.setMainOrderNo(request.getMainOrderNo());
        orderRecord.setPrimOrdrNo(prim_ordr_no);
        orderRecord.setCmdtyOrderNo(subRequest.getCmdtyOrderNo());
        orderRecord.setPyTrnNo(py_trn_no);
        orderRecord.setSubOrdrId(sub_ordr_id);
        orderRecord.setRatio(subRequest.getRatio());
        orderRecord.setStatus(0);
        orderRecord.setCommissionFlag(subRequest.getCommissionFlag());
        orderRecord.setCreateTime(now);
        orderRecord.setUpdateTime(now);
        orderRecord.setCommission(subRequest.getCommission());
        orderRecord.setTotalAmt(subRequest.getTotalAmt());
        orderRecord.setFreight(subRequest.getFreight());
        orderRecord.setFreightCommission(subRequest.getFreightCommission());
        orderRecord.setExtra(subRequest.getExtra());
        orderRecord.setExtraCommission(subRequest.getExtraCommission());
        return orderRecord;
    }

    public String getWxOpenId(String jsCode){
        try{
            //发送请求
            HttpClient httpClient = new HttpClient(WX_OPENID_URL,1000,3000);
            Map<String, String> jsonRequestData = new HashMap<>();
            jsonRequestData.put("appid", ccbConfig.getWxAppid());
            jsonRequestData.put("secret", ccbConfig.getWxSecret());
            jsonRequestData.put("js_code", jsCode);
            jsonRequestData.put("grant_type", "authorization_code");
            String resultStr =  httpClient.sendSring(jsonRequestData,"UTF-8");
            JSONObject jsonObject  = JSONObject.parseObject(resultStr);
            String openid = jsonObject.getString("openid");
            if (StringUtils.isBlank(openid)) {
                log.error("========建行支付，获取微信openId失败：{}", jsonObject);
                throw new SbcRuntimeException("K-000001");
            }
            return openid;
        }catch (Exception e){
            log.error("========建行支付，获取微信openId失败：", e);
            throw new SbcRuntimeException("K-000001");
        }
    }

    public JSONObject buildgGatherByMergePlaceorderJson(CcbPayOrderRequest request) {

        String ittpartyStmId = ccbConfig.getIttpartyStmId();
        String pyChnlCd = ccbConfig.getPyChnlCd();
        String mktId = ccbConfig.getMktId();

        if (Objects.isNull(request.getClrgDt())) {
            Long clearDateRule = ccbConfig.getClearDateRule();
            request.setClrgDt(DateUtil.format(LocalDateTime.now().plusDays(clearDateRule), DateUtil.FMT_TIME_5));
        }

        //发起方时间戳
        String Ittparty_Tms = DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmssSSS");
        //发起方流水号
        String Ittparty_Jrnl_No = System.currentTimeMillis() + "";

        //支付方式代码：01 PC端   02 线下支付（无收银台） 03 移动端H5页面 (app)
        //05 微信小程序（无收银台）06 对私网银（无收银台） 07 聚合二维码（无收银台）
        String Pymd_Cd = "03";

        //订单类型:02 消费券购买订单 03 在途订单 04 普通订单  05 线下订单
        String Py_Ordr_Tpcd = "04";
        //币种
        String Ccy = "156";

        //版本号
        String Vno = "4";

        JSONObject json = new JSONObject(true);
        //发起方渠道编号
        json.put("Ittparty_Stm_Id", ittpartyStmId);
        //发起方渠道代码
        json.put("Py_Chnl_Cd", pyChnlCd);
        //发起方时间戳
        json.put("Ittparty_Tms", Ittparty_Tms);
        //发起方流水号 不许重复
        json.put("Ittparty_Jrnl_No", Ittparty_Jrnl_No);
        //市场编号
        json.put("Mkt_Id", mktId);
        //主订单编号
        json.put("Main_Ordr_No", request.getMainOrderNo());

        Pymd_Cd = getCcbPymdCd(request.getPayType());

        // 微信小程序
        if (StringUtils.equals("05", Pymd_Cd)) {
            String wxOpenId = getWxOpenId(request.getJsCode());
            json.put("Sub_Appid", ccbConfig.getWxAppid());
            json.put("Sub_Openid", wxOpenId);
        }

        // 支付宝小程序
        if (StringUtils.equals("14", Pymd_Cd)) {
            String userId = getAlipayUserId(request.getJsCode());
            json.put("Sub_Appid", userId);
        }

        //支付方式代码
        json.put("Pymd_Cd", Pymd_Cd);
        //订单类型
        json.put("Py_Ordr_Tpcd", Py_Ordr_Tpcd);
        //币种
        json.put("Ccy", Ccy);
        //订单总金额
        json.put("Ordr_Tamt", request.getOrderAmount().toString());
        //交易总金额
        json.put("Txn_Tamt", request.getTxnAmt().toString());
        //手续费承担方,若指定手续费承担方则添加
        json.put("Hdcg_Brs_Id", ccbConfig.getMktMrchId());
        // 回调地址序号
        json.put("Py_Rslt_Ntc_Sn", ccbConfig.getResultNotifySn());
        json.put("Clrg_Dt", request.getClrgDt());
        //版本号
        json.put("Vno", Vno);

        JSONArray orderListJsonArray1 = new JSONArray();
        request.getSubOrderList().forEach(subOrder -> {
            // 鲸币充值
            if (Objects.equals(4, request.getPayType()) || Objects.equals(8, request.getPayType())) {
                subOrder.setMktMrchtId(ccbConfig.getCoinMktMrchId());
            }
            //子订单
            JSONObject order1 = new JSONObject();
            if (Objects.equals(subOrder.getCommissionFlag(), CcbSubOrderType.COMMISSION)) {
                subOrder.setMktMrchtId(ccbConfig.getMktMrchId());
            }
            if (Objects.equals(subOrder.getCommissionFlag(), CcbSubOrderType.FREIGHT_COMMISSION)) {
                subOrder.setMktMrchtId(ccbConfig.getFreightMktMrchId());
            }
            // 广告充值
            if (Objects.equals(7, request.getPayType())) {
                subOrder.setMktMrchtId(ccbConfig.getAdMktMrchId());
            }
            //商家编号
            order1.put("Mkt_Mrch_Id", subOrder.getMktMrchtId());
            //商品订单号
            order1.put("Cmdty_Ordr_No", subOrder.getCmdtyOrderNo());
            //订单金额
            order1.put("Ordr_Amt", subOrder.getOrderAmount());
            //交易金额
            order1.put("Txnamt", subOrder.getTxnAmt());
            //附加项总金额
            order1.put("Apd_Tamnt", subOrder.getApdAmt());

            orderListJsonArray1.add(order1);

        });
        // 手续费承担方不能为空 无佣金时，手续费承担方不能为空
        boolean b = request.getSubOrderList().stream().anyMatch(sub -> Objects.equals(CcbSubOrderType.COMMISSION, sub.getCommissionFlag()));
        if (!b) {
            json.put("Hdcg_Brs_Id",request.getSubOrderList().get(0).getMktMrchtId());
        }

        json.put("Orderlist", orderListJsonArray1);
        return json;
    }

    public String getAlipayUserId(String jsCode) {
        String appPrivateKey = ccbConfig.getAliPrivateKey();
        String aliPayPublicKey = ccbConfig.getAliPublicKey();
        String aliAppid = ccbConfig.getAliAppid();

        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl("https://openapi.alipay.com/gateway.do");
        alipayConfig.setAppId(aliAppid);
        alipayConfig.setPrivateKey(appPrivateKey);
        alipayConfig.setFormat("json");
        alipayConfig.setAlipayPublicKey(aliPayPublicKey);
        alipayConfig.setCharset("UTF8");
        alipayConfig.setSignType("RSA2");
        // AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", aliAppid, appPrivateKey, "json", "UTF-8", aliPayPublicKey, "RSA2");
        try {
            AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);
            AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
            request.setCode(jsCode);
            request.setGrantType("authorization_code");
            AlipaySystemOauthTokenResponse response = alipayClient.execute(request);
            log.info("========建行支付，获取支付宝UserId返回：{}", JSON.toJSONString(response));
            String userId = response.getUserId();
            if (StringUtils.isBlank(userId)) {
                log.error("========建行支付，获取支付宝UserId失败");
                throw new SbcRuntimeException("K-000001");
            }
            return userId;
        } catch (Exception e) {

            log.error("========建行支付，获取支付宝UserId失败：", e);
            throw new SbcRuntimeException("K-000001");
        }

    }


    public AlipayTradeQueryResponse alipayTradeQuery(String tradeNo) {
        String appPrivateKey = ccbConfig.getAliPrivateKey();
        String aliPayPublicKey = ccbConfig.getAliPublicKey();
        String aliAppid = ccbConfig.getAliAppid();

        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl("https://openapi.alipay.com/gateway.do");
        alipayConfig.setAppId(aliAppid);
        alipayConfig.setPrivateKey(appPrivateKey);
        alipayConfig.setFormat("json");
        alipayConfig.setAlipayPublicKey(aliPayPublicKey);
        alipayConfig.setCharset("UTF8");
        alipayConfig.setSignType("RSA2");
        try {
            AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            JSONObject bizContent = new JSONObject();
            bizContent.put("trade_no", tradeNo);
            request.setBizContent(bizContent.toString());
            return alipayClient.execute(request);
        } catch (Exception e) {
            log.error("========查询支付宝订单状态失败：", e);
            throw new SbcRuntimeException("K-000001");
        }

    }


    /**
     * 建行退单接口
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String refundOrder(CcbRefundRequest request) {

        List<CcbPayOrderRecord> payOrderRecordList = new ArrayList<>();
        PartRfndDo partRfndDo = getPartRfndDo(request, payOrderRecordList);

        String jsonString = CcbSplicingUtil.createSign(JSON.toJSONString(partRfndDo,new PascalNameFilter()));
        String signInf = CcbRSASignUtil.sign(ccbConfig.getPrivateRsa(), jsonString);
        partRfndDo.setSign_Inf(signInf);
        String json = JSON.toJSONString(partRfndDo,new PascalNameFilter());

        CcbLog ccbLog = new CcbLog();
        ccbLog.setBusinessId(request.getCustRfndTrcno());
        ccbLog.setRequestType(2);
        ccbLog.setRequest(json);
        ccbLog.setCreateTime(LocalDateTime.now());

        try {
            log.info("建行退单接口调用,参数：{}", json);
            String result = CcbHttpUtil.doJsonPost(CCB_URL + CCB_REFUND_URL, json);
            log.info("建行退单接口调用,返回：{}", result);
            //开始验签
            JSONObject jsonObject = JSONObject.parseObject(result);

            ccbLog.setResponse(jsonObject.toJSONString());

            //验签原串
            jsonString = CcbSplicingUtil.createSign(result);
            signInf = jsonObject.getString("Sign_Inf");
            boolean verifySign = CcbRSASignUtil.verifySign(ccbConfig.getPlatPk(), jsonString, signInf);
            if (verifySign) {
                // 退单
                JSONObject obj = JSONObject.parseObject(result);
                String refund_Rsp_St = obj.getString("Refund_Rsp_St");
                if (!Objects.equals(refund_Rsp_St, "00")) {
                    log.info("建行退单接口调用返回失败：{}", obj);
                    // save CcbRefundRetry
                    saveCcbRefundRetry(request, json, jsonObject.toJSONString(), refund_Rsp_St);

                }else {
                    String rfnd_trcno = obj.getString("Rfnd_Trcno");

                    if (StringUtils.isNotBlank(rfnd_trcno)) {
                        CcbPayRecord ccbPayRecord = ccbPayRecordRepository.findByPyTrnNo(request.getPayTrnNo());
                        BigDecimal refundAmount = ccbPayRecord.getRefundAmount();
                        if (Objects.isNull(refundAmount)) {
                            refundAmount = BigDecimal.ZERO;
                        }
                        ccbPayRecord.setRefundAmount(refundAmount.add(request.getRfndAmt()));
                        ccbPayRecord.setUpdateTime(LocalDateTime.now());
                        ccbPayRecordRepository.saveAndFlush(ccbPayRecord);
                        if (CollectionUtils.isNotEmpty(payOrderRecordList)) {
                            ccbPayOrderRecordRepository.saveAll(payOrderRecordList);
                        }
                    }
                }
                updateCcbRefundStatus(request.getRid(), refund_Rsp_St);
                return result;
            } else {
                log.error("建行退单接口调用,验签失败。");
                // throw new SbcRuntimeException("K-000001");
            }
        } catch (Exception e) {
            saveCcbRefundRetry(request, json, null, "99");
            log.error("建行退单接口调用失败", e);
            // throw new SbcRuntimeException("K-000001");
        }finally {
            ccbLogService.saveLog(ccbLog);
        }
        return null;
    }

    private void updateCcbRefundStatus(String rid, String refund_Rsp_St) {
        int refundStatus = 3;
        if (Objects.equals(refund_Rsp_St, "00")) {
            refundStatus = 1;
        } else if (Objects.equals(refund_Rsp_St, "02")) {
            refundStatus = 2;
        }
        ccbRefundRecordRepository.updateRefundStatusByRid(refundStatus, rid);
    }

    private void saveCcbRefundRetry(CcbRefundRequest request, String json, String response, String refund_Rsp_St) {
        String rid = request.getCustRfndTrcno();
        CcbRefundRetry ccbRefundRetry = ccbRefundRetryRepository.findByRid(rid);
        LocalDateTime now = LocalDateTime.now();
        if (Objects.isNull(ccbRefundRetry)) {
            ccbRefundRetry  = new CcbRefundRetry();
            ccbRefundRetry.setCreateTime(now);
        }
        ccbRefundRetry.setRid(rid);
        ccbRefundRetry.setTid(request.getTid());
        ccbRefundRetry.setCustRfndTrcno(rid);
        ccbRefundRetry.setRetryCount(0);
        ccbRefundRetry.setRequest(json);
        ccbRefundRetry.setResponse(response);
        ccbRefundRetry.setRefundRspSt(refund_Rsp_St);
        ccbRefundRetry.setRfndAmt(request.getRfndAmt());
        ccbRefundRetry.setPyTrnNo(request.getPayTrnNo());
        ccbRefundRetry.setRefundFreight(request.getRefundFreight());
        ccbRefundRetry.setFreightPrice(request.getFreightPrice());
        ccbRefundRetry.setExtraPrice(request.getExtraPrice());
        ccbRefundRetry.setUpdateTime(now);
        ccbRefundRetryService.save(ccbRefundRetry);
    }


    public PartRfndDo createRefundDo(CcbRefundRequest request) {
        String ittpartyStmId = ccbConfig.getIttpartyStmId();
        String pyChnlCd = ccbConfig.getPyChnlCd();
        String mktId = ccbConfig.getMktId();

        PartRfndDo partRfndDo = new PartRfndDo();
        partRfndDo.setMkt_Id(mktId);
        partRfndDo.setPy_Chnl_Cd(pyChnlCd);
        partRfndDo.setIttparty_Stm_Id(ittpartyStmId);
        partRfndDo.setPy_Trn_No(request.getPayTrnNo());
        partRfndDo.setVno("3");
        BigDecimal orderRfndAmt = request.getRfndAmt().setScale(2, RoundingMode.HALF_UP);
        partRfndDo.setRfnd_Amt(orderRfndAmt);
        partRfndDo.setCust_Rfnd_Trcno(request.getCustRfndTrcno());

        List<PartRfndDoSubDo> subDoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(request.getSubOrderList())) {
            for (CcbRefundSubRequest sub : request.getSubOrderList()) {
                String subOrdrId = sub.getSubOrderId();
                PartRfndDoSubDo partRfndDoSubDo = new PartRfndDoSubDo();
                partRfndDoSubDo.setSub_Ordr_Id(subOrdrId);
                BigDecimal subOrderRfndAmt = sub.getSubOrderRfndAmt();
                partRfndDoSubDo.setRfnd_Amt(subOrderRfndAmt);
                // partRfndDoSubDo.setParlist(map3.get(subOrdrId));
                subDoList.add(partRfndDoSubDo);
            }

        }
        partRfndDo.setSub_Ordr_List(subDoList);
        return partRfndDo;

    }

    @Transactional(rollbackFor = Exception.class)
    @LcnTransaction
    public String ccbPayRecordSuccess(String pyTrnNo) {
        CcbPayRecord record = ccbPayRecordRepository.findByPyTrnNo(pyTrnNo);
        if (Objects.isNull(record)) {
            throw new RuntimeException("建行支付记录不存在");
        }
        LocalDateTime now = LocalDateTime.now();
        record.setStatus(1);
        record.setUpdateTime(now);
        ccbPayRecordRepository.saveAndFlush(record);
        List<CcbPayOrderRecord> recordList = ccbPayOrderRecordRepository.findByPyTrnNo(pyTrnNo);

        if (CollectionUtils.isNotEmpty(recordList)) {
            for (CcbPayOrderRecord orderRecord : recordList) {
                orderRecord.setStatus(1);
                orderRecord.setUpdateTime(now);
                ccbPayOrderRecordRepository.saveAndFlush(orderRecord);
            }
        }

        // save pay_trade_record
        PayTradeRecord payTradeRecord = recordRepository.findByBusinessId(record.getBusinessId());

        if (Objects.nonNull(payTradeRecord) && payTradeRecord.getStatus() == TradeStatus.SUCCEED) {
            return record.getBusinessId();
        }

        if (Objects.isNull(payTradeRecord)) {
            payTradeRecord = new PayTradeRecord();
            payTradeRecord.setId(GeneratorUtils.generatePT());
        }

        payTradeRecord.setApplyPrice(record.getOrdrTamt());
        payTradeRecord.setBusinessId(record.getBusinessId());
        payTradeRecord.setChannelItemId(32L);
        payTradeRecord.setTradeType(TradeType.PAY);
        payTradeRecord.setCreateTime(LocalDateTime.now());
        payTradeRecord.setStatus(TradeStatus.PROCESSING);
        payTradeRecord.setPayOrderNo(record.getMainOrdrNo());
        payTradeRecord.setChargeId(record.getPrimOrdrNo());
        payTradeRecord.setTradeNo(record.getPyTrnNo());

        recordRepository.saveAndFlush(payTradeRecord);


        return record.getBusinessId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void ccbRefundSuccess(String custRfndTrcno) {
        PayTradeRecord record = recordRepository.findByBusinessId(custRfndTrcno);
        if (Objects.nonNull(record)) {
            record.setStatus(TradeStatus.SUCCEED);
            record.setCallbackTime(LocalDateTime.now());
            recordRepository.saveAndFlush(record);
        }
        // 处理retry表
        CcbRefundRetry retry = ccbRefundRetryRepository.findByRid(custRfndTrcno);
        if (Objects.nonNull(retry)) {
            retry.setRefundRspSt("00");
            retry.setUpdateTime(LocalDateTime.now());
            JSONObject obj = new JSONObject();
            obj.put("Refund_Rsp_St", "00");
            retry.setResponse(obj.toJSONString());
            ccbRefundRetryRepository.saveAndFlush(retry);
            if (custRfndTrcno.startsWith("AD")) {
                CcbPayRecord payRecord = ccbPayRecordRepository.findByPyTrnNo(retry.getPyTrnNo());
                if (Objects.nonNull(payRecord)) {
                    BigDecimal refundAmount = Objects.isNull(payRecord.getRefundAmount()) ? BigDecimal.ZERO : payRecord.getRefundAmount();
                    payRecord.setRefundAmount(refundAmount.add(retry.getRfndAmt()));
                    payRecord.setUpdateTime(LocalDateTime.now());
                    ccbPayRecordRepository.saveAndFlush(payRecord);
                }
            }
            // 单独退运费
            if (custRfndTrcno.startsWith("F") || custRfndTrcno.startsWith("E")) {
                this.addRefundAmt(retry.getTid(), retry.getRid(), retry.getPyTrnNo());
            }
        }
        updateCcbRefundStatus(custRfndTrcno, "00");
    }

    public void addRefundAmt(String tid, String pyTrnNo, String rid) {
        CcbRefundRecord refundRecord = ccbRefundRecordRepository.findByRid(rid);
        if (Objects.nonNull(refundRecord)) {
            List<CcbPayOrderRecord> updateList = new ArrayList<>();
            List<CcbPayOrderRecord> payOrderRecordList = ccbPayOrderRecordRepository.findByPyTrnNo(pyTrnNo);

            if (refundRecord.getMktPrice().compareTo(BigDecimal.ZERO) > 0) {
                // 商家
                payOrderRecordList.stream().filter(r -> Objects.equals(r.getCommissionFlag(), CcbSubOrderType.MERCHANT) && r.getBusinessId().equals(tid)).findFirst().ifPresent(r ->{
                    BigDecimal refundAmount = Objects.isNull(r.getRefundAmount()) ? BigDecimal.ZERO : r.getRefundAmount();
                    r.setRefundAmount(refundAmount.add(refundRecord.getFreightCommissionPrice()));
                    r.setUpdateTime(LocalDateTime.now());
                    updateList.add(r);
                });
            }
            if (refundRecord.getFreightPrice().compareTo(BigDecimal.ZERO) > 0) {
                // 承运商
                payOrderRecordList.stream().filter(r -> Objects.equals(r.getCommissionFlag(), CcbSubOrderType.FREIGHT) && r.getBusinessId().equals(tid)).findFirst().ifPresent(r -> {
                    BigDecimal refundAmount = Objects.isNull(r.getRefundAmount()) ? BigDecimal.ZERO : r.getRefundAmount();
                    r.setRefundAmount(refundAmount.add(refundRecord.getFreightPrice()));
                    r.setUpdateTime(LocalDateTime.now());
                    updateList.add(r);
                });
            }

            if (refundRecord.getFreightCommissionPrice().compareTo(BigDecimal.ZERO) > 0) {
                // 承运商佣金
                payOrderRecordList.stream().filter(r -> Objects.equals(r.getCommissionFlag(), CcbSubOrderType.FREIGHT_COMMISSION)).findFirst().ifPresent(r -> {
                    BigDecimal refundAmount = Objects.isNull(r.getRefundAmount()) ? BigDecimal.ZERO : r.getRefundAmount();
                    r.setRefundAmount(refundAmount.add(refundRecord.getFreightCommissionPrice()));
                    r.setUpdateTime(LocalDateTime.now());
                    updateList.add(r);
                });
            }

            if (refundRecord.getCommissionPrice().compareTo(BigDecimal.ZERO) > 0) {
                // 佣金
                payOrderRecordList.stream().filter(r -> Objects.equals(r.getCommissionFlag(), CcbSubOrderType.COMMISSION)).findFirst().ifPresent(r -> {
                    BigDecimal refundAmount = Objects.isNull(r.getRefundAmount()) ? BigDecimal.ZERO : r.getRefundAmount();
                    r.setRefundAmount(refundAmount.add(refundRecord.getCommissionPrice()));
                    r.setUpdateTime(LocalDateTime.now());
                    updateList.add(r);
                });
            }

            if (refundRecord.getExtraPrice().compareTo(BigDecimal.ZERO) > 0) {
                // 运费加收
                payOrderRecordList.stream().filter(r -> Objects.equals(r.getCommissionFlag(), CcbSubOrderType.EXTRA) && r.getBusinessId().equals(tid)).findFirst().ifPresent(r -> {
                    BigDecimal refundAmount = Objects.isNull(r.getRefundAmount()) ? BigDecimal.ZERO : r.getRefundAmount();
                    r.setRefundAmount(refundAmount.add(refundRecord.getExtraPrice()));
                    r.setUpdateTime(LocalDateTime.now());
                    updateList.add(r);
                });
            }
            if (CollectionUtils.isNotEmpty(updateList)) {
                ccbPayOrderRecordRepository.saveAll(updateList);
            }
            if (refundRecord.getRefundPrice().compareTo(BigDecimal.ZERO) > 0) {
                CcbPayRecord payRecord = ccbPayRecordRepository.findByPyTrnNo(pyTrnNo);
                if (Objects.nonNull(payRecord)) {
                    BigDecimal refundAmount = Objects.isNull(payRecord.getRefundAmount()) ? BigDecimal.ZERO : payRecord.getRefundAmount();
                    payRecord.setRefundAmount(refundAmount.add(refundRecord.getRefundPrice()));
                    payRecord.setUpdateTime(LocalDateTime.now());
                    ccbPayRecordRepository.saveAndFlush(payRecord);
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void syncCcbSubAccountStatus() {
        // 查询一个月内 应分账的数据
        LocalDateTime yesterday  = LocalDateTime.now().minusDays(1);
        LocalDateTime lastMonth = yesterday.minusMonths(1);
        String yesterdayStr = DateUtil.format(yesterday, DateUtil.FMT_TIME_5);
        String lastMonthStr = DateUtil.format(lastMonth, DateUtil.FMT_TIME_5);

        //List<CcbPayRecord> list = ccbPayRecordRepository.findSubAccountData(yesterdayStr, lastMonthStr, "00");
        List<CcbPayRecord> list = ccbPayRecordRepository.findAll((Specification<CcbPayRecord>) (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("status"), 1));
            predicates.add(builder.greaterThanOrEqualTo(root.get("clrgDt"), lastMonthStr));
            predicates.add(builder.lessThanOrEqualTo(root.get("clrgDt"), yesterdayStr));
            // predicates.add(builder.or(builder.isNull(root.get("subAccStcd")), builder.notEqual(root.get("subAccStcd"), "00")));
            predicates.add(builder.isNull(root.get("subAccStcd")));

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        });

        if (CollectionUtils.isNotEmpty(list)) {
            for (CcbPayRecord record : list) {
                queryCcbSubAccountStatus(record.getPyTrnNo());
            }
        }

    }

    public void queryCcbSubAccountStatus(String pyTrnNo) {

        String ittpartyStmId = ccbConfig.getIttpartyStmId();
        String pyChnlCd = ccbConfig.getPyChnlCd();
        String mktId = ccbConfig.getMktId();


        String formatter = DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmssSSS");
        //发起方时间戳
        String Ittparty_Tms = formatter;
        //发起方流水号
        String Ittparty_Jrnl_No = System.currentTimeMillis() + "";

        JSONObject json = new JSONObject(true);
        //发起方渠道编号
        json.put("Ittparty_Stm_Id", ittpartyStmId);
        //发起方渠道代码
        json.put("Py_Chnl_Cd", pyChnlCd);
        //发起方时间戳
        json.put("Ittparty_Tms", Ittparty_Tms);
        //发起方流水号 不许重复
        json.put("Ittparty_Jrnl_No", Ittparty_Jrnl_No);
        //市场编号
        json.put("Mkt_Id", mktId);

        json.put("Py_Trn_No", pyTrnNo);

        json.put("Vno", "5");

        String jsonString = CcbSplicingUtil.createSign(json.toString());

        String signInf = CcbRSASignUtil.sign(ccbConfig.getPrivateRsa(), jsonString);
        json.put("Sign_Inf", signInf);

        log.info("========建行查询分账状态，入参：{}", json);
        try {
            String result = CcbHttpUtil.doJsonPost(CCB_URL + CCB_SUB_ACCOUNT_STATUS_URL, json.toString());
            log.info("========建行查询分账状态，返回：{}", result);
            //开始验签
            JSONObject jsonObject = JSONObject.parseObject(result);
            //验签原串
            jsonString = CcbSplicingUtil.createSign(result);
            signInf = jsonObject.getString("Sign_Inf");

            boolean verifySign = CcbRSASignUtil.verifySign(ccbConfig.getPlatPk(), jsonString, signInf);
            if (!verifySign) {
                log.error("========建行查询分账状态，验签失败。");
                throw new SbcRuntimeException("K-000001");
            }

            String subAccStcd = jsonObject.getString("Sub_Acc_Stcd");
            // 更新记录表
            ccbPayRecordRepository.updateSubAccStcdAndUpdateTimeByPyTrnNo(subAccStcd, LocalDateTime.now(), pyTrnNo);
            JSONArray clrglist = jsonObject.getJSONArray("Clrglist");
            if (CollectionUtils.isNotEmpty(clrglist)) {
                for (int i = 0; i < clrglist.size(); i++) {
                    JSONObject subObj = clrglist.getJSONObject(i);
                    String subOrdrId = subObj.getString("Sub_Ordr_Id");
                    String clrgDt = subObj.getString("Clrg_Dt");
                    String clrgStcd = subObj.getString("Clrg_Stcd");
                    ccbPayOrderRecordRepository.updateClrgStcdAndClrgDtAndUpdateTimeBySubOrdrId(clrgStcd, clrgDt, LocalDateTime.now(), subOrdrId);
                }
            }

        } catch (Exception e) {
            log.error("========建行查询分账状态，错误：", e);
            throw new SbcRuntimeException("K-000001");
        }
    }


    public Boolean validCcbMerchantNo(String ccbMerchantNo) {
        if (StringUtils.equals(ccbConfig.getMktMrchId(), ccbMerchantNo)
                || StringUtils.equals(ccbConfig.getCoinMktMrchId(), ccbMerchantNo)
                || StringUtils.equals(ccbConfig.getFreightMktMrchId(), ccbMerchantNo)
                || StringUtils.equals(ccbConfig.getAdMktMrchId(), ccbMerchantNo)
        ) {
            return false;
        }
        return ccbBusinessRepository.existsByMktMrchIdAndDelFlag(ccbMerchantNo, CcbDelFlag.NO);
    }

    /**
     * 确认收货接口
     * @param primOrdrNo
     */
    @Transactional(rollbackFor = Exception.class)
    public void confirmReceipt(String primOrdrNo) {

        JSONObject json = new JSONObject(true);
        //发起方渠道编号
        json.put("Ittparty_Stm_Id", ccbConfig.getIttpartyStmId());
        //发起方渠道代码
        json.put("Py_Chnl_Cd", ccbConfig.getPyChnlCd());
        //发起方时间戳
        json.put("Ittparty_Tms", DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmssSSS"));
        //发起方流水号 不许重复
        json.put("Ittparty_Jrnl_No", System.currentTimeMillis() + "");
        //市场编号
        json.put("Mkt_Id", ccbConfig.getMktId());
        //主订单编号
        json.put("Prim_Ordr_No", primOrdrNo);
        //版本号
        json.put("Vno", "4");

        try {
            String jsonString = CcbSplicingUtil.createSign(json.toString());
            String signInf = CcbRSASignUtil.sign(ccbConfig.getPrivateRsa(), jsonString);
            json.put("Sign_Inf", signInf);
            log.info("========建行支付确认收货，入参：{}", json);
            String result = CcbHttpUtil.doJsonPost(CCB_URL + CCB_CONFIRM_RECEIPT, json.toString());
            log.info("========建行支付确认收货，返回：{}", result);
            /**
             * {"Rsp_Inf":"确认收货处理完成","Svc_Rsp_Cd":"","Svc_Rsp_St":"00"}
             */

        } catch (Exception e) {
            log.error("========建行支付确认收货，错误", e);
            // throw new SbcRuntimeException("K-000001");
        }
    }

    public CcbPayOrderRecord queryCcbPayOrderRecord(String tradeId, String payOrderNo) {
        return ccbPayOrderRecordRepository.findByBusinessIdAndMainOrderNoAndCommissionFlag(tradeId, payOrderNo, CcbSubOrderType.MERCHANT);
    }

    public String queryBusinessIdByPyTrnNo(String pyTrnNo) {
        CcbPayRecord record = ccbPayRecordRepository.findByPyTrnNo(pyTrnNo);
        if (Objects.nonNull(record)) {
            return record.getBusinessId();
        }
        return null;
    }

    public String retryRefund(String rid) {
        // 查询retry记录 判断当前状态
        CcbRefundRetry retryOrg = ccbRefundRetryRepository.findByRid(rid);
        CcbRefundRetry retry = KsBeanUtil.convert(retryOrg, CcbRefundRetry.class);
        String refundRspSt = retry.getRefundRspSt();
        if (Objects.equals("00", refundRspSt)) {
            return retry.getResponse();
        }
        String queryResult = queryRefundStatus(retry.getCustRfndTrcno());
        JSONObject obj = JSONObject.parseObject(queryResult);
        String refund_rsp_st =  obj.getString("Refund_Rsp_St");
        // 00成功 01失败 02等待 03异常
        if (!refundRspSt.equals(refund_rsp_st)) {
            retry.setRefundRspSt(refund_rsp_st);
            retry.setUpdateTime(LocalDateTime.now());
            retry.setResponse(queryResult);
            ccbRefundRetryService.save(retry);
        }
        if (Objects.equals("00", refund_rsp_st)) {
            updateCcbRefundStatus(rid, refund_rsp_st);
            return queryResult;
        }

        // 重试
        if (Objects.equals("01", refund_rsp_st)) {
            // 230816 建行要求 分账后的订单暂不支持退款
            // 230926 去除限制
            // CcbPayRecord ccbPayRecord = ccbPayRecordRepository.findByPyTrnNo(retry.getPyTrnNo());
            // String clrgDt = ccbPayRecord.getClrgDt();
            // String todayStr = DateUtil.format(LocalDateTime.now(), DateUtil.FMT_TIME_5);
            // log.info("建行退款重试，退单号：{}，日期：{}，应分账日期：{}", rid, todayStr, clrgDt);
            // if (Integer.parseInt(todayStr) > Integer.parseInt(clrgDt)) {
            //     throw new SbcRuntimeException("K-100215", new Object[]{"建行已分账，暂不支持线上退款，请线下处理。"});
            // }

            // 生成新的客户退款流水 调建重试退款
            int retryCount = retry.getRetryCount() + 1;
            String custRfndTrcno = retry.getRid() + "T" + retryCount;
            CcbRefundRequest request = CcbRefundRequest.builder()
                    .payTrnNo(retry.getPyTrnNo())
                    .custRfndTrcno(custRfndTrcno)
                    .rfndAmt(retry.getRfndAmt())
                    .tid(retry.getTid())
                    .refundFreight(retry.getRefundFreight())
                    .freightPrice(retry.getFreightPrice())
                    .extraPrice(retry.getExtraPrice())
                    .rid(retry.getRid())
                    .build();

            retry.setCustRfndTrcno(custRfndTrcno);
            retry.setRetryCount(retryCount);
            String result = this.refundOrderRetry(request,retry);
            log.info("建行退款重试返回：{}", result);
            if (Objects.nonNull(result)) {
                return result;
            }else {
                log.info("建行重试退款失败:");
            }

        }else {
            return retry.getResponse();
        }

        return null;
    }

    public String refundOrderRetry(CcbRefundRequest request, CcbRefundRetry retry) {

        List<CcbPayOrderRecord> payOrderRecordList = new ArrayList<>();
        PartRfndDo partRfndDo = getPartRfndDo(request, payOrderRecordList);

        String jsonString = CcbSplicingUtil.createSign(JSON.toJSONString(partRfndDo,new PascalNameFilter()));
        String signInf = CcbRSASignUtil.sign(ccbConfig.getPrivateRsa(), jsonString);
        partRfndDo.setSign_Inf(signInf);
        String json = JSON.toJSONString(partRfndDo,new PascalNameFilter());

        CcbLog ccbLog = new CcbLog();
        ccbLog.setBusinessId(request.getCustRfndTrcno());
        ccbLog.setRequestType(2);
        ccbLog.setRequest(json);
        ccbLog.setCreateTime(LocalDateTime.now());

        try {
            log.info("建行退单接口调用,参数：{}", json);
            String result = CcbHttpUtil.doJsonPost(CCB_URL + CCB_REFUND_URL, json);
            log.info("建行退单接口调用,返回：{}", result);
            //开始验签
            JSONObject jsonObject = JSONObject.parseObject(result);
            ccbLog.setResponse(jsonObject.toJSONString());

            //验签原串
            jsonString = CcbSplicingUtil.createSign(result);
            signInf = jsonObject.getString("Sign_Inf");
            boolean verifySign = CcbRSASignUtil.verifySign(ccbConfig.getPlatPk(), jsonString, signInf);
            if (verifySign) {
                // 退单
                JSONObject obj = JSONObject.parseObject(result);
                String refund_Rsp_St = obj.getString("Refund_Rsp_St");
                if (!Objects.equals(refund_Rsp_St, "00")) {
                    log.info("建行退单接口调用返回失败：{}", obj);
                }else {
                    CcbPayRecord ccbPayRecord = ccbPayRecordRepository.findByPyTrnNo(request.getPayTrnNo());
                    BigDecimal refundAmount = ccbPayRecord.getRefundAmount();
                    if (Objects.isNull(refundAmount)) {
                        refundAmount = BigDecimal.ZERO;
                    }
                    ccbPayRecord.setRefundAmount(refundAmount.add(request.getRfndAmt()));
                    ccbPayRecord.setUpdateTime(LocalDateTime.now());
                    ccbPayRecordRepository.saveAndFlush(ccbPayRecord);
                    if (CollectionUtils.isNotEmpty(payOrderRecordList)) {
                        ccbPayOrderRecordRepository.saveAll(payOrderRecordList);
                    }
                }
                retry.setRequest(json);
                retry.setRefundRspSt(refund_Rsp_St);
                retry.setResponse(result);
                retry.setUpdateTime(LocalDateTime.now());
                ccbRefundRetryService.save(retry);
                updateCcbRefundStatus(request.getRid(), refund_Rsp_St);
                return result;
            } else {
                log.error("建行退单接口调用,验签失败。");
            }
        } catch (Exception e) {
            log.error("建行退单接口调用失败", e);
        }finally {
            ccbLogService.saveLog(ccbLog);
        }
        return null;
    }

    private PartRfndDo getPartRfndDo(CcbRefundRequest request, List<CcbPayOrderRecord> payOrderRecordList) {
        if (StringUtils.isNotBlank(request.getTid())) {

            CcbPayOrderRecord payOrderRecordOrg = ccbPayOrderRecordRepository.findByBusinessIdAndPyTrnNoAndCommissionFlag(request.getTid(), request.getPayTrnNo(), CcbSubOrderType.MERCHANT);
            CcbPayOrderRecord payOrderRecord = KsBeanUtil.convert(payOrderRecordOrg, CcbPayOrderRecord.class);

            // 填充子订单
            if (Objects.nonNull(payOrderRecord)) {
                List<CcbRefundSubRequest> subRequests = new ArrayList<>();

                BigDecimal freight = BigDecimal.ZERO;
                BigDecimal mktPrice = request.getRfndAmt();
                BigDecimal extraPrice = BigDecimal.ZERO;
                if (Objects.nonNull(request.getRefundFreight()) && request.getRefundFreight()) {
                    freight = request.getFreightPrice();
                    mktPrice = request.getRfndAmt().subtract(request.getFreightPrice().setScale(2, RoundingMode.DOWN));
                }
                if (Objects.nonNull(request.getExtraPrice()) && request.getExtraPrice().compareTo(BigDecimal.ZERO) > 0) {
                    extraPrice = request.getExtraPrice();
                    mktPrice = mktPrice.subtract(extraPrice).setScale(2, RoundingMode.DOWN);
                }

                BigDecimal amt = BigDecimal.ZERO;
                if (mktPrice.compareTo(BigDecimal.ZERO) > 0) {
                    // 退款总金额 乘以 比例
                    BigDecimal ratio = payOrderRecord.getRatio();

                    amt = mktPrice.multiply(ratio).setScale(2, RoundingMode.UP);

                    if (amt.compareTo(payOrderRecord.getOrderAmount()) > 0) {
                        amt = payOrderRecord.getOrderAmount();
                    }
                    BigDecimal refundAmount = payOrderRecord.getRefundAmount();
                    if (Objects.isNull(refundAmount)) {
                        refundAmount = BigDecimal.ZERO;
                    }
                    BigDecimal canReturnAmt = payOrderRecord.getOrderAmount().subtract(refundAmount);
                    if (amt.compareTo(canReturnAmt) > 0) {
                        amt = canReturnAmt;
                    }
                    payOrderRecord.setRefundAmount(refundAmount.add(amt));
                    payOrderRecord.setUpdateTime(LocalDateTime.now());
                    payOrderRecordList.add(payOrderRecord);

                    // 退商家金额
                    CcbRefundSubRequest subRequest = new CcbRefundSubRequest();
                    subRequest.setSubOrderId(payOrderRecord.getSubOrdrId());
                    subRequest.setSubOrderRfndAmt(amt);
                    subRequests.add(subRequest);

                    log.info("建行退款退单号：{}，退商家金额：{}", request.getCustRfndTrcno(), amt);
                }


                // 退商家佣金
                BigDecimal returnMktCommission = mktPrice.subtract(amt);
                // 退运费总佣金部分
                BigDecimal returnFreightCommission = BigDecimal.ZERO;

                BigDecimal freightPrice = BigDecimal.ZERO;

                BigDecimal freightCommission = BigDecimal.ZERO;

                CcbPayOrderRecord freightCommissionRecord = null;
                BigDecimal freightRefundAmount = BigDecimal.ZERO;

                // 退运费
                if (freight.compareTo(BigDecimal.ZERO) > 0) {
                    // 退还运费
                    CcbPayOrderRecord freightRecordOrg = ccbPayOrderRecordRepository.findByBusinessIdAndPyTrnNoAndCommissionFlag(request.getTid(), request.getPayTrnNo(), CcbSubOrderType.FREIGHT);
                    CcbPayOrderRecord freightRecord = KsBeanUtil.convert(freightRecordOrg, CcbPayOrderRecord.class);
                    if (Objects.nonNull(freightRecord)) {

                        BigDecimal freightReturnPrice = freightRecord.getOrderAmount();
                        freightCommission = freightRecord.getFreightCommission();

                        // 退运费佣金
                        CcbPayOrderRecord freightCommissionRecordOrg = ccbPayOrderRecordRepository.findByPyTrnNoAndCommissionFlag(request.getPayTrnNo(), CcbSubOrderType.FREIGHT_COMMISSION);
                        freightCommissionRecord = KsBeanUtil.convert(freightCommissionRecordOrg, CcbPayOrderRecord.class);
                        if (Objects.nonNull(freightCommissionRecord)) {
                            freightRefundAmount = freightCommissionRecord.getRefundAmount();
                            freightRefundAmount = Objects.isNull(freightRefundAmount) ? BigDecimal.ZERO : freightRefundAmount;
                            BigDecimal canReturnFreightCommission = freightCommissionRecord.getOrderAmount().subtract(freightRefundAmount);
                            if (freightCommission.compareTo(canReturnFreightCommission) > 0) {
                                freightCommission = canReturnFreightCommission;
                            }
                        }

                        BigDecimal totalAmt = freightRecord.getTotalAmt();
                        if (freight.compareTo(totalAmt) < 0) {
                            // 退部分运费
                            BigDecimal freightRatio = freightRecord.getRatio();
                            // 承运商退款部分
                            freightReturnPrice = freight.multiply(freightRatio).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal otherPrice = freight.subtract(freightReturnPrice);
                            // 承运商运费退款部分
                            if (otherPrice.compareTo(freightCommission) <= 0) {
                                freightCommission = otherPrice;
                            }else {
                                // 运费总佣金部分
                                BigDecimal diff = otherPrice.subtract(freightCommission);
                                if (diff.compareTo(freightRecord.getCommission()) <= 0) {
                                    returnFreightCommission = diff;
                                }
                            }
                        }else {
                            // 运费总佣金部分
                            returnFreightCommission = freightRecord.getCommission();
                        }
                        freightRecord.setRefundAmount(freightReturnPrice);
                        freightRecord.setUpdateTime(LocalDateTime.now());
                        payOrderRecordList.add(freightRecord);

                        CcbRefundSubRequest freightSubRequest = new CcbRefundSubRequest();
                        freightSubRequest.setSubOrderId(freightRecord.getSubOrdrId());
                        freightSubRequest.setSubOrderRfndAmt(freightReturnPrice);
                        subRequests.add(freightSubRequest);
                        log.info("建行退款退单号：{}，退承运商运费金额：{}", request.getCustRfndTrcno(), freightReturnPrice);
                        freightPrice = freightReturnPrice;
                    }
                }

                BigDecimal freightExtraCommission = BigDecimal.ZERO;
                BigDecimal extraCommission = BigDecimal.ZERO;
                BigDecimal freightExtraPrice = BigDecimal.ZERO;
                if (extraPrice.compareTo(BigDecimal.ZERO) > 0) {
                    // 退运费加收
                    CcbPayOrderRecord extraRecordOrg = ccbPayOrderRecordRepository.findByBusinessIdAndPyTrnNoAndCommissionFlag(request.getTid(), request.getPayTrnNo(), CcbSubOrderType.EXTRA);
                    CcbPayOrderRecord extraRecord = KsBeanUtil.convert(extraRecordOrg, CcbPayOrderRecord.class);
                    if (Objects.nonNull(extraRecord)) {
                        freightExtraPrice = extraRecord.getOrderAmount();
                        freightExtraCommission = extraRecord.getExtraCommission();
                        extraCommission = extraRecord.getCommission();

                        extraRecord.setRefundAmount(freightExtraPrice);
                        extraRecord.setUpdateTime(LocalDateTime.now());
                        payOrderRecordList.add(extraRecord);

                        CcbRefundSubRequest extraSubRequest = new CcbRefundSubRequest();
                        extraSubRequest.setSubOrderId(extraRecord.getSubOrdrId());
                        extraSubRequest.setSubOrderRfndAmt(freightExtraPrice);
                        subRequests.add(extraSubRequest);

                        log.info("建行退款退单号：{}，退承运商运费加收金额：{}", request.getCustRfndTrcno(), freightExtraPrice);
                    }
                }

                // 承运商佣金
                freightCommission = freightCommission.add(freightExtraCommission);
                // BigDecimal freightCommission = freightRecord.getFreightCommission();
                if (freightCommission.compareTo(BigDecimal.ZERO) > 0 ) {
                    if (Objects.isNull(freightCommissionRecord)) {
                        CcbPayOrderRecord freightCommissionRecordOrg = ccbPayOrderRecordRepository.findByPyTrnNoAndCommissionFlag(request.getPayTrnNo(), CcbSubOrderType.FREIGHT_COMMISSION);
                        freightCommissionRecord = KsBeanUtil.convert(freightCommissionRecordOrg, CcbPayOrderRecord.class);
                    }
                    // 退运费佣金
                    if (Objects.nonNull(freightCommissionRecord)) {
                        BigDecimal freightRefund = freightCommissionRecord.getRefundAmount();
                        freightRefund = Objects.isNull(freightRefund) ? BigDecimal.ZERO : freightRefund;
                        freightCommissionRecord.setRefundAmount(freightRefund.add(freightCommission));
                        freightCommissionRecord.setUpdateTime(LocalDateTime.now());
                        payOrderRecordList.add(freightCommissionRecord);

                        CcbRefundSubRequest freightCommissionSubRequest = new CcbRefundSubRequest();
                        freightCommissionSubRequest.setSubOrderId(freightCommissionRecord.getSubOrdrId());
                        freightCommissionSubRequest.setSubOrderRfndAmt(freightCommission);
                        subRequests.add(freightCommissionSubRequest);
                        log.info("建行退款退单号：{}，退运费佣金金额：{}", request.getCustRfndTrcno(), freightCommission);
                    }
                }

                BigDecimal totalReturnCommission = returnMktCommission.add(returnFreightCommission).add(extraCommission);
                if (totalReturnCommission.compareTo(BigDecimal.ZERO) > 0) {
                    CcbPayOrderRecord commissionRecordOrg = ccbPayOrderRecordRepository.findByPyTrnNoAndCommissionFlag(request.getPayTrnNo(), CcbSubOrderType.COMMISSION);
                    CcbPayOrderRecord commissionRecord = KsBeanUtil.convert(commissionRecordOrg, CcbPayOrderRecord.class);

                    if (Objects.nonNull(commissionRecord)) {
                        BigDecimal commissionRefundAmount = commissionRecord.getRefundAmount();
                        if (Objects.isNull(commissionRefundAmount)) {
                            commissionRefundAmount = BigDecimal.ZERO;
                        }
                        commissionRecord.setRefundAmount(commissionRefundAmount.add(totalReturnCommission));
                        commissionRecord.setUpdateTime(LocalDateTime.now());
                        payOrderRecordList.add(commissionRecord);
                        CcbRefundSubRequest commissionSubRequest = new CcbRefundSubRequest();
                        commissionSubRequest.setSubOrderId(commissionRecord.getSubOrdrId());
                        commissionSubRequest.setSubOrderRfndAmt(totalReturnCommission);
                        subRequests.add(commissionSubRequest);
                        log.info("建行退款退单号：{}，退总佣金金额：{}", request.getCustRfndTrcno(), totalReturnCommission);
                    }
                }
                // 校验计算金额
                BigDecimal totalAmt = subRequests.stream().map(CcbRefundSubRequest::getSubOrderRfndAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (totalAmt.compareTo(request.getRfndAmt()) != 0) {
                    log.info("建行退款，退单号:{},计算金额错误,应退金额：{}，计算金额：{}", request.getCustRfndTrcno(), request.getRfndAmt(), totalAmt);
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "建行退款，退单号" + request.getCustRfndTrcno() + "计算退款金额失败");
                }

                request.setSubOrderList(subRequests.stream().filter(o -> o.getSubOrderRfndAmt().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList()));

                // 保存退款记录
                saveCcbRefundRecord(request, payOrderRecord, amt, freightPrice, freightCommission, totalReturnCommission, freight, extraPrice, freightExtraPrice);
            }
        }

        return createRefundDo(request);
    }

    public void saveCcbRefundRecord(CcbRefundRequest request, CcbPayOrderRecord payOrderRecord, BigDecimal amt, BigDecimal freightPrice, BigDecimal freightCommissionPrice,
                                    BigDecimal totalReturnCommission, BigDecimal freight, BigDecimal extraPrice,BigDecimal freightExtraPrice) {

        CcbRefundRecord ccbRefundRecord = ccbRefundRecordRepository.findByRid(request.getRid());
        LocalDateTime now = LocalDateTime.now();
        if (Objects.isNull(ccbRefundRecord)) {
            ccbRefundRecord = new CcbRefundRecord();
            ccbRefundRecord.setCreateTime(now);
        }
        ccbRefundRecord.setRid(request.getRid());
        ccbRefundRecord.setTid(request.getTid());
        ccbRefundRecord.setMainOrderNo(payOrderRecord.getMainOrderNo());
        ccbRefundRecord.setPyTrnNo(payOrderRecord.getPyTrnNo());
        ccbRefundRecord.setRefundPrice(request.getRfndAmt());
        ccbRefundRecord.setMktPrice(amt);
        ccbRefundRecord.setFreightPrice(freightPrice);
        ccbRefundRecord.setFreightCommissionPrice(freightCommissionPrice);
        ccbRefundRecord.setCommissionPrice(totalReturnCommission);
        ccbRefundRecord.setUpdateTime(now);
        ccbRefundRecord.setRefundFreight(freight);
        ccbRefundRecord.setTotalExtra(extraPrice);
        ccbRefundRecord.setExtraPrice(freightExtraPrice);
        ccbRefundRecordRepository.saveAndFlush(ccbRefundRecord);
    }

    private String queryRefundStatus(String custRfndTrcno) {
        // 查询建行退单状态 更新退款状态
        JSONObject json = new JSONObject(true);
        //发起方渠道编号
        json.put("Ittparty_Stm_Id", ccbConfig.getIttpartyStmId());
        //发起方渠道代码
        json.put("Py_Chnl_Cd", ccbConfig.getPyChnlCd());
        //发起方时间戳
        json.put("Ittparty_Tms", DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmssSSS"));
        //发起方流水号 不许重复
        json.put("Ittparty_Jrnl_No", System.currentTimeMillis() + "");
        //市场编号
        json.put("Mkt_Id", ccbConfig.getMktId());
        json.put("Vno", "4");
        json.put("Cust_Rfnd_Trcno", custRfndTrcno);

        String jsonString = CcbSplicingUtil.createSign(json.toString());
        String signInf = CcbRSASignUtil.sign(ccbConfig.getPrivateRsa(), jsonString);
        json.put("Sign_Inf", signInf);
        log.info("========建行查询退款状态，入参：{}", json);
        String result = CcbHttpUtil.doJsonPost(CCB_URL + CCB_REFUND_QUERY, json.toString());
        log.info("========建行查询退款状态，返回：{}", json);
        return result;
    }


    public CcbPayRecord queryCcbPayRecordByPayOrderNo(String payOrderNo) {
        return ccbPayRecordRepository.findByMainOrdrNo(payOrderNo);
    }

    public CcbPayRecord queryCcbPayRecordByPyTrnNo(String pyTrnNo) {
        return ccbPayRecordRepository.findByPyTrnNo(pyTrnNo);
    }

    public void saveCcbPayImg(CcbPayImgRequest request) {
        ccbPayRecordRepository.updateCcbPayImgByMainOrdrNo(request.getCcbPayImg(), request.getPayOrderNo());
    }

    public CcbRefundRecord queryCcbRefundRecordByRid(String rid) {
        return ccbRefundRecordRepository.findByRidAndRefundStatus(rid, 1);
    }

    public CcbBusiness queryCcbBusinessByName(String name) {
        List<CcbBusiness> list = ccbBusinessRepository.findByMktMrchNmAndDelFlagOrderByCreateTimeDesc(name, CcbDelFlag.NO);
        return list.stream().filter(o -> !StringUtils.equals(ccbConfig.getMktMrchId(), o.getMrchCrdtNo())
                && !StringUtils.equals(ccbConfig.getCoinMktMrchId(), o.getMrchCrdtNo())
                && !StringUtils.equals(ccbConfig.getFreightMktMrchId(), o.getMrchCrdtNo())
                && !StringUtils.equals(ccbConfig.getAdMktMrchId(), o.getMrchCrdtNo())
                ).findFirst().orElse(null);
    }

    @Transactional(rollbackFor = Exception.class)
    public CcbRefundAdResponse adRefund(CcbRefundAdRequest request) {
        String pyTrnNo = request.getPyTrnNo();
        CcbPayRecord payRecord = ccbPayRecordRepository.findByPyTrnNo(pyTrnNo);
        if (Objects.isNull(payRecord) || !Objects.equals(payRecord.getStatus(), 1)) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单未支付");
        }
        BigDecimal refundAmount = Objects.isNull(payRecord.getRefundAmount()) ? BigDecimal.ZERO : payRecord.getRefundAmount();
        BigDecimal canRefundAmount = payRecord.getOrdrTamt().subtract(refundAmount);
        if (Objects.nonNull(request.getRefundAmount()) && request.getRefundAmount().compareTo(canRefundAmount) > 0) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "超出可退金额");
        }

        BigDecimal amount = Objects.nonNull(request.getRefundAmount()) ? request.getRefundAmount() : canRefundAmount;
        CcbRefundRequest refundRequest = CcbRefundRequest.builder().payTrnNo(pyTrnNo).custRfndTrcno(request.getRefundNo()).rfndAmt(amount).build();

        String result;
        Boolean existRetry = ccbRefundRetryService.existRetryByRid(request.getRefundNo());
        if (existRetry) {
            result = this.retryRefund(request.getRefundNo());
            log.info("建行退款重试返回：{}", result);
        }else {
            result = this.refundOrder(refundRequest);
        }

        CcbRefundAdResponse response = new CcbRefundAdResponse();
        if (Objects.nonNull(result)) {
            JSONObject obj = JSONObject.parseObject(result);
            String refund_Rsp_St = obj.getString("Refund_Rsp_St");
            if (Objects.equals("00", refund_Rsp_St)) {
                // 退款成功
                response.setRefundStatus(1);
            }else {
                String errMsg = obj.getString("Refund_Rsp_Inf");
                if (Objects.nonNull(errMsg)) {
                    response.setFailedMsg(errMsg);
                }
                if (Objects.equals("02", refund_Rsp_St)) {
                    response.setRefundStatus(2);
                }else {
                    response.setRefundStatus(3);
                }
            }
        }else {
            response.setRefundStatus(3);
        }
        return response;
    }

    @Transactional(rollbackFor = Exception.class)
    public CcbRefundFreightResponse freightRefund(CcbRefundFreightRequest request) {
        CcbPayRecord payRecord = ccbPayRecordRepository.findByMainOrdrNo(request.getPayOrderNo());
        if (Objects.isNull(payRecord) || !Objects.equals(payRecord.getStatus(), 1)) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单未支付");
        }

        CcbPayOrderRecord freightRecord = ccbPayOrderRecordRepository.findByBusinessIdAndMainOrderNoAndCommissionFlag(request.getTid(), request.getPayOrderNo(), CcbSubOrderType.FREIGHT);
        if (Objects.isNull(freightRecord)) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单不支持退运费");
        }

        // 总运费
        BigDecimal totalAmt = freightRecord.getTotalAmt();

        List<CcbRefundRecord> refundRecordList = ccbRefundRecordRepository.findByTidAndRefundStatus(request.getTid(), 1);
        BigDecimal refundedFreight = refundRecordList.stream().map(CcbRefundRecord::getRefundFreight).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal refundFreight = totalAmt.subtract(refundedFreight);
        if (refundFreight.compareTo(BigDecimal.ZERO) <= 0) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单已无运费可退");
        }
        BigDecimal amount = request.getAmount();
        if (amount.compareTo(refundFreight) > 0) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "超出可退运费金额");
        }

        CcbRefundRequest refundRequest = CcbRefundRequest
                .builder()
                .payTrnNo(payRecord.getPyTrnNo())
                .custRfndTrcno(request.getRid())
                .tid(request.getTid())
                .rid(request.getRid())
                .rfndAmt(amount)
                .refundFreight(true)
                .freightPrice(amount)
                .build();

        String result;
        Boolean existRetry = ccbRefundRetryService.existRetryByRid(request.getRid());
        if (existRetry) {
            result = this.retryRefund(request.getRid());
            log.info("建行运费退款重试返回：{}", result);
        }else {
            result = this.refundOrder(refundRequest);
        }

        CcbRefundFreightResponse response = new CcbRefundFreightResponse();
        response.setRefundFreightPrice(amount);
        if (Objects.nonNull(result)) {
            JSONObject obj = JSONObject.parseObject(result);
            String refund_Rsp_St = obj.getString("Refund_Rsp_St");
            if (Objects.equals("00", refund_Rsp_St)) {
                // 退款成功
                response.setRefundStatus(1);
            }else {
                if (Objects.equals("02", refund_Rsp_St)) {
                    response.setRefundStatus(2);
                }else {
                    response.setRefundStatus(3);
                }
            }
        }else {
            response.setRefundStatus(3);
        }
        return response;
    }

    @Transactional(rollbackFor = Exception.class)
    public CcbRefundFreightResponse extraRefund(CcbRefundExtraRequest request) {
        CcbPayRecord payRecord = ccbPayRecordRepository.findByMainOrdrNo(request.getPayOrderNo());
        if (Objects.isNull(payRecord) || !Objects.equals(payRecord.getStatus(), 1)) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单" + request.getTid() + "未支付");
        }
        CcbPayOrderRecord extraRecord = ccbPayOrderRecordRepository.findByBusinessIdAndMainOrderNoAndCommissionFlag(request.getTid(), request.getPayOrderNo(), CcbSubOrderType.EXTRA);
        if (Objects.isNull(extraRecord)) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单" + request.getTid() + "不支持退加收运费");
        }
        // 总加收记录
        BigDecimal totalAmt = extraRecord.getTotalAmt();
        //
        List<CcbRefundRecord> refundRecordList = ccbRefundRecordRepository.findByTidAndRefundStatus(request.getTid(), 1);
        BigDecimal totalRefundExtraPrice = refundRecordList
                .stream()
                .map(CcbRefundRecord::getTotalExtra)
                .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalRefundExtraPrice.compareTo(BigDecimal.ZERO) > 0) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单" + request.getTid() + "运费加收金额已被退款");
        }

        //
        CcbRefundRequest refundRequest = CcbRefundRequest
                .builder()
                .payTrnNo(payRecord.getPyTrnNo())
                .custRfndTrcno(request.getRid())
                .tid(request.getTid())
                .rid(request.getRid())
                .rfndAmt(totalAmt)
                .extraPrice(totalAmt)
                .build();

        String result;
        Boolean existRetry = ccbRefundRetryService.existRetryByRid(request.getRid());
        if (existRetry) {
            result = this.retryRefund(request.getRid());
            log.info("建行运费退款重试返回：{}", result);
        }else {
            result = this.refundOrder(refundRequest);
        }

        CcbRefundFreightResponse response = new CcbRefundFreightResponse();
        response.setRefundFreightPrice(totalAmt);
        if (Objects.nonNull(result)) {
            JSONObject obj = JSONObject.parseObject(result);
            String refund_Rsp_St = obj.getString("Refund_Rsp_St");
            if (Objects.equals("00", refund_Rsp_St)) {
                // 退款成功
                response.setRefundStatus(1);
            }else {
                if (Objects.equals("02", refund_Rsp_St)) {
                    response.setRefundStatus(2);
                }else {
                    response.setRefundStatus(3);
                }
            }
        }else {
            response.setRefundStatus(3);
        }
        return response;
    }

    public CcbPayStatusQueryResponse ccbPayStatusQuery(String py_Trn_No) {
        JSONObject json = new JSONObject(true);
        //发起方渠道编号
        json.put("Ittparty_Stm_Id", ccbConfig.getIttpartyStmId());
        //发起方渠道代码
        json.put("Py_Chnl_Cd", ccbConfig.getPyChnlCd());
        //发起方时间戳
        json.put("Ittparty_Tms", DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmssSSS"));
        //发起方流水号 不许重复
        json.put("Ittparty_Jrnl_No", System.currentTimeMillis() + "");
        //市场编号
        json.put("Mkt_Id", ccbConfig.getMktId());
        //支付流水号
        json.put("Py_Trn_No", py_Trn_No);
        //版本号
        json.put("Vno", "4");
        String jsonString = CcbSplicingUtil.createSign(json.toString());
        String signInf = CcbRSASignUtil.sign(ccbConfig.getPrivateRsa(), jsonString);
        json.put("Sign_Inf", signInf);
        try {
            log.info("查询建行支付状态，入参：{}", json);
            String result = CcbHttpUtil.doJsonPost(CCB_URL + CCB_PAY_QUERY_URL, json.toString());;
            log.info("查询建行支付状态，返回：{}", json);
            //开始验签
            JSONObject jsonObject = JSONObject.parseObject(result);
            //验签原串
            jsonString = CcbSplicingUtil.createSign(result);
            signInf = jsonObject.getString("Sign_Inf");
            boolean verifySign = CcbRSASignUtil.verifySign(ccbConfig.getPlatPk(), jsonString, signInf);
            if (verifySign) {
                return JSON.toJavaObject(jsonObject, CcbPayStatusQueryResponse.class);
            }else {
                log.error("查询建行支付状态,验签失败。");
            }
        } catch (Exception e) {
            log.error("查询建行支付状态失败", e);
        }
        return null;
    }
}
