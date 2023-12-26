package com.wanmi.sbc.order.returnorder.service;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.account.bean.enums.PayWay;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.GeneratorService;
import com.wanmi.sbc.common.util.HttpCommonResult;
import com.wanmi.sbc.common.util.HttpCommonUtil;
import com.wanmi.sbc.goods.bean.enums.SaleType;
import com.wanmi.sbc.order.bean.enums.ClaimsApplyType;
import com.wanmi.sbc.order.api.request.trade.CoinActivityPushKingdeeRequest;
import com.wanmi.sbc.order.common.OrderCommonService;
import com.wanmi.sbc.order.enums.PushKingdeeStatusEnum;
import com.wanmi.sbc.order.returnorder.model.root.NewPileReturnOrder;
import com.wanmi.sbc.order.returnorder.model.root.ReturnOrder;
import com.wanmi.sbc.order.returnorder.model.root.TradePushKingdeeRefund;
import com.wanmi.sbc.order.returnorder.repository.TradePushKingdeeRefundRepository;
import com.wanmi.sbc.order.trade.model.entity.value.*;
import com.wanmi.sbc.order.trade.model.newPileTrade.NewPileTrade;
import com.wanmi.sbc.order.trade.model.root.Trade;
import com.wanmi.sbc.order.trade.model.root.TradePushKingdeePayOrder;
import com.wanmi.sbc.order.trade.repository.TradePushKingdeePayRepository;
import com.wanmi.sbc.order.util.KingdeeLoginUtils;
import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author chenchang
 * @since 2023/04/19 10:55
 */
@Slf4j
@Service
public class PushKingdeeService {


    @Value("${kingdee.login.url}")
    private String loginUrl;

    /**
     * 退货
     */
    @Value("${kingdee.Return.url}")
    private String returnUrl;

    /**
     * 退款
     */
    @Value("${kingdee.Refurn.url}")
    private String refundUrl;

    @Value("${kingdee.orderInvalid.url}")
    private String orderInvalidUrl;

    @Value("${kingdee.user}")
    private String kingdeeUser;

    @Value("${kingdee.pwd}")
    private String kingdeePwd;

    @Value("${kingdee.organization}")
    private String kingdeeOrganization;

    @Value("${kingdee.wechat}")
    private String kingdeeWechat;

    @Value("${kingdee.alipay}")
    private String kingdeeAlipay;

    @Value("${kingdee.pay.url}")
    private String payUrl;

    /**
     * 囤货退货
     */
    @Value("${kingdee.stockUpRet.url}")
    private String stockUpRetUrl;

    /**
     * 是否开启新金蝶
     */
    @Value("${kingdee.open.state}")
    private Boolean kingdeeOpenState;

    @Autowired
    private KingdeeLoginUtils kingdeeLoginUtils;

    @Autowired
    private TradePushKingdeeRefundRepository tradePushKingdeeRefundRepository;

    @Autowired
    GeneratorService generatorService;

    @Autowired
    private TradePushKingdeePayRepository tradePushKingdeePayRepository;

    @Autowired
    private OrderCommonService orderCommonService;

    /**
     * 向金蝶push支付单
     */
    public void pushPayOrderKingdee(String lpRecordNo,String tradeId,String account, BigDecimal price,String loginToken,boolean isDecute) {
        if(isDecute){
            return;
        }
        //log.info("returnOrder.pushPayOrderKingdee req {}", JSONObject.toJSONString(returnOrder));
        //查询支付记录中是否有支付单
        TradePushKingdeePayOrder tradePushKingdeePayOrder = new TradePushKingdeePayOrder();
        tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.CREATE.toStatus());
        Integer number = tradePushKingdeePayRepository.selectPushKingdeePayOrderNumber(lpRecordNo);
        try {
            KingdeePayOrder payOrder = new KingdeePayOrder();
            payOrder.setFDate(DateUtil.nowDate());
            payOrder.setOrderNumber(lpRecordNo);
            payOrder.setFSaleNum(tradeId);

            Map FCustId = new HashMap();
            FCustId.put("FNumber", account);
            payOrder.setFCustId(FCustId);
            Map FPAYORGID = new HashMap();
            FPAYORGID.put("FNumber", kingdeeOrganization);
            payOrder.setFPAYORGID(FPAYORGID);//收款组织
            //收款方式
            payOrder.setFColType("线上");
            List<KingdeePayOrderSettlement> freceivebillentry = new ArrayList<>();
            KingdeePayOrderSettlement payOrderSettlement = new KingdeePayOrderSettlement();
            Map FSETTLETYPEID = new HashMap();
            FSETTLETYPEID.put("FNumber", "QB");

            payOrderSettlement.setFSETTLETYPEID(FSETTLETYPEID);//结算方式
            payOrderSettlement.setFRECTOTALAMOUNTFOR(price.toString());
            //销售订单号
            payOrderSettlement.setF_ora_YDDH(tradeId);
            Map FACCOUNTID = new HashMap();
            FACCOUNTID.put("FNumber", account);
            payOrderSettlement.setFACCOUNTID(FACCOUNTID);
            //线下支付将收款银行信息推到金蝶
            freceivebillentry.add(payOrderSettlement);
            payOrder.setFRECEIVEBILLENTRY(freceivebillentry);
            //登录财务系统
            if (StringUtils.isNotEmpty(loginToken)) {
                //提交财务单
                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("Model", payOrder);
                log.info("鲸币充值收款单推金蝶---实际推送内容-->" + JSONObject.toJSONString(payOrder));
                HttpCommonResult httpCommonResult = HttpCommonUtil.postHeader(payUrl, requestMap, loginToken);
                KingDeeResult kingDeeResult = JSONObject.parseObject(httpCommonResult.getResultData(), KingDeeResult.class);
                log.info("TradeService.pushPayOrderKingdee result1:{} code:{}", httpCommonResult.getResultData(), kingDeeResult.getCode());
                if (Objects.nonNull(kingDeeResult) && kingDeeResult.getCode().equals("0")) {
                    tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.PUSHSUCCESS.toStatus());
                } else {
                    tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
                }
                tradePushKingdeePayOrder.setInstructions(httpCommonResult.getResultData());
            } else {
                log.error("TradeService.pushPayOrderKingdee push kingdee error");
                String res = "金蝶登录失败";
                tradePushKingdeePayOrder.setInstructions(res);
                tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
            }
        } catch (Exception e) {
            log.error("TradeService.pushPayOrderKingdee error:{}", e);
            String res = "金蝶推送失败";
            tradePushKingdeePayOrder.setInstructions(res);
            tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
        } finally {
            if (number == 0) {
                tradePushKingdeePayOrder.setPayCode(lpRecordNo);
                tradePushKingdeePayOrder.setOrderCode(tradeId);
                tradePushKingdeePayOrder.setPayType(PayWay.BALANCE.toValue());
                tradePushKingdeePayOrder.setCreateTime(LocalDateTime.now());
                tradePushKingdeePayOrder.setUpdateTime(LocalDateTime.now());
                //申请金额
                tradePushKingdeePayOrder.setPracticalPrice(price);
                tradePushKingdeePayRepository.saveAndFlush(tradePushKingdeePayOrder);
            } else {
                tradePushKingdeePayOrder.setUpdateTime(LocalDateTime.now());
                tradePushKingdeePayOrder.setPayCode(lpRecordNo);
                tradePushKingdeePayRepository.updatePushKingdeePayOrderState(tradePushKingdeePayOrder);
            }
        }
    }

    public void pushRefundOrderKingdeeForClaims(Trade trade, ReturnOrder returnOrder) {
        Assert.notNull(trade, "trade must not be null");
        Assert.notNull(returnOrder, "returnOrder must not be null");
        Assert.notNull(returnOrder.getId(), "returnOrder.getId() must not be null");
        log.info("pushRefundOrderKingdeeForClaims，tid:{}", trade.getId());
        if(!orderCommonService.erpCanTrade(trade)){
            return;
        }
        String lpRecordNo = returnOrder.getId();
        TradePushKingdeeRefund pushKingdeeRefundOrder = new TradePushKingdeeRefund();
        pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.CREATE.toStatus());
        try {
            if (!checkRefundOrderKingdee(trade, returnOrder, pushKingdeeRefundOrder)) {
                pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.PARAMETERERROR.toStatus());
                return;
            }
            KingdeeRefundOrder payOrder = new KingdeeRefundOrder();
            payOrder.setFDate(DateUtil.nowDate());
            payOrder.setOrderNumber(lpRecordNo);
            payOrder.setFSaleNum(trade.getId());

            Map<String, Object> FCustId = new HashMap<>();
            FCustId.put("FNumber", trade.getBuyer().getAccount());
            payOrder.setFCustId(FCustId);

            Map<String, Object> FPAYORGID = new HashMap<>();
            FPAYORGID.put("FNumber", kingdeeOrganization);
            payOrder.setFPAYORGID(FPAYORGID);

            List<KingdeeRefundOrderSettlement> freceivebillentry = new ArrayList<>();
            KingdeeRefundOrderSettlement payOrderSettlement = new KingdeeRefundOrderSettlement();
            String refundComment;
            if (StringUtils.isEmpty(returnOrder.getDescription())) {
                log.info("PushKingdeeService.pushRefundOrderKingdeeForClaims Lack FNOTE");
                refundComment = "理赔退款";
            } else {
                refundComment = returnOrder.getDescription();
            }
            Map<String, Object> FSETTLETYPEID = new HashMap<>();
            FSETTLETYPEID.put("FNumber", "QB");

            //使用银行卡
            Map<String, Object> FACCOUNTID = new HashMap<>();
            FACCOUNTID.put("FNumber", "QB");
            payOrderSettlement.setFACCOUNTID(FACCOUNTID);
            payOrderSettlement.setFSETTLETYPEID(FSETTLETYPEID);

            BigDecimal price = returnOrder.getReturnPrice().getApplyPrice().setScale(2, RoundingMode.DOWN);
            payOrderSettlement.setFRECTOTALAMOUNTFOR(price.toString());

            payOrderSettlement.setFNOTE(refundComment + "-" + lpRecordNo);
            freceivebillentry.add(payOrderSettlement);
            payOrder.setFRECEIVEBILLENTRY(freceivebillentry);
            //销售类型
            Map<String, String> fSaleType = new HashMap<>();
            if (Objects.nonNull(trade.getSaleType())) {
                fSaleType.put("FNumber", String.valueOf(trade.getSaleType().toValue()));
                payOrder.setFSaleType(fSaleType);
            } else {
                fSaleType.put("FNumber", String.valueOf(SaleType.WHOLESALE.toValue()));
                payOrder.setFSaleType(fSaleType);
            }

            //登录财务系统
            String loginToken = loginERP();
            if (StringUtils.isEmpty(loginToken)) {
                pushKingdeeRefundOrder.setInstructions("金蝶登录失败");
                pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
                return;
            }
            //推销售单
            pushPayOrderKingdee(lpRecordNo,trade.getId(),trade.getBuyer().getAccount(),price,loginToken,ClaimsApplyType.DEDUCTION_RECHARGE.getId().equals(returnOrder.getChaimApllyType()));
            //提交财务单
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("Model", payOrder);
            HttpCommonResult refundResult = HttpCommonUtil.postHeader(refundUrl, requestMap, loginToken);
            KingDeeResult kingDeeResult = JSONObject.parseObject(refundResult.getResultData(), KingDeeResult.class);
            log.info("PushKingdeeService.pushRefundOrderKingdeeForClaims result1:{}  code:{}", refundResult.getResultData(), refundResult.getResultCode());
            if (Objects.nonNull(kingDeeResult) && kingDeeResult.getCode().equals("0")) {
                pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.PUSHSUCCESS.toStatus());
            } else {
                pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
            }
            pushKingdeeRefundOrder.setInstructions(refundResult.getResultData());

        } catch (Exception e) {
            log.error("PushKingdeeService.pushRefundOrderKingdeeForClaims error:", e);
            String res = "金蝶推送失败";
            pushKingdeeRefundOrder.setInstructions(res);
            pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
        } finally {
            Integer number = tradePushKingdeeRefundRepository.selcetPushKingdeeRefundNumber(lpRecordNo);
            //没有创建，有就更新
            if (number == 0) {
                pushKingdeeRefundOrder.setOrderCode(trade.getId());
                pushKingdeeRefundOrder.setRefundCode(lpRecordNo);
                pushKingdeeRefundOrder.setPayType(PayWay.BALANCE.toValue());
                pushKingdeeRefundOrder.setCreateTime(LocalDateTime.now());
                pushKingdeeRefundOrder.setUpdateTime(LocalDateTime.now());
                pushKingdeeRefundOrder.setCustomerAccount(trade.getBuyer().getAccount());
                tradePushKingdeeRefundRepository.saveAndFlush(pushKingdeeRefundOrder);
            } else {
                pushKingdeeRefundOrder.setUpdateTime(LocalDateTime.now());
                pushKingdeeRefundOrder.setRefundCode(lpRecordNo);
                tradePushKingdeeRefundRepository.updatePushKingdeeRefundOrderState(pushKingdeeRefundOrder);
            }
        }
    }

    private String loginERP() {
        Map<String, Object> requestLogMap = new HashMap<>();
        requestLogMap.put("user", kingdeeUser);
        requestLogMap.put("pwd", kingdeePwd);
        return kingdeeLoginUtils.userLoginKingdee(requestLogMap, loginUrl);
    }

    private boolean checkRefundOrderKingdee(Trade trade, ReturnOrder returnOrder, TradePushKingdeeRefund pushKingdeeRefundOrder) {
        if (StringUtils.isEmpty(trade.getId())) {
            log.info("PushKingdeeService.pushRefundOrderKingdeeForClaims Lack Tid");
            pushKingdeeRefundOrder.setInstructions("Lack Tid");
            return false;
        }
        if (StringUtils.isEmpty(trade.getBuyer().getAccount())) {
            log.info("PushKingdeeService.pushRefundOrderKingdeeForClaims Lack FCustId");
            pushKingdeeRefundOrder.setInstructions("Lack FCustId");
            return false;
        }
//        if (StringUtils.isEmpty(returnOrder.getId())) {
//            log.info("PushKingdeeService.pushRefundOrderKingdeeForClaims Lack ReturnOrderCode");
//            pushKingdeeRefundOrder.setInstructions("Lack ReturnOrderCode");
//            return false;
//        }
//        if (!(payWay == PayWay.CASH) && StringUtils.isEmpty(refundOrder.getRefundBill().getPayChannel())) {
//            log.info("PushKingdeeService.pushRefundOrderKingdeeForClaims Lack FSETTLETYPEID");
//            pushKingdeeRefundOrder.setInstructions("Lack FSETTLETYPEID");
//            return false;
//        }
        if (returnOrder.getReturnPrice().getApplyPrice() == null) {
            log.info("PushKingdeeService.pushRefundOrderKingdeeForClaims Lack FRECTOTALAMOUNTFOR");
            pushKingdeeRefundOrder.setInstructions("Lack FRECTOTALAMOUNTFOR");
            return false;
        }
        return true;
    }

    public void pushRefundOrderKingdeeForClaims(NewPileTrade trade, NewPileReturnOrder returnOrder) {
        Assert.notNull(trade, "trade must not be null");
        Assert.notNull(returnOrder, "returnOrder must not be null");
        Assert.notNull(returnOrder.getId(), "returnOrder.getId() must not be null");
        if(!orderCommonService.erpCanPileTrade(trade)){
            return;
        }
        log.info("pushRefundOrderKingdeeForClaims，tid:{}", trade.getId());
        String lpRecordNo = returnOrder.getId();
        TradePushKingdeeRefund pushKingdeeRefundOrder = new TradePushKingdeeRefund();
        pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.CREATE.toStatus());
        try {
            if (!checkRefundOrderKingdee(trade, returnOrder, pushKingdeeRefundOrder)) {
                pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.PARAMETERERROR.toStatus());
                return;
            }
            KingdeeRefundOrder payOrder = new KingdeeRefundOrder();
            payOrder.setFDate(DateUtil.nowDate());
            payOrder.setOrderNumber(lpRecordNo);
            payOrder.setFSaleNum(trade.getId());

            Map<String, Object> FCustId = new HashMap<>();
            FCustId.put("FNumber", trade.getBuyer().getAccount());
            payOrder.setFCustId(FCustId);

            Map<String, Object> FPAYORGID = new HashMap<>();
            FPAYORGID.put("FNumber", kingdeeOrganization);
            payOrder.setFPAYORGID(FPAYORGID);

            List<KingdeeRefundOrderSettlement> freceivebillentry = new ArrayList<>();
            KingdeeRefundOrderSettlement payOrderSettlement = new KingdeeRefundOrderSettlement();
            String refundComment;
            if (StringUtils.isEmpty(returnOrder.getDescription())) {
                log.info("PushKingdeeService.pushRefundOrderKingdeeForClaims Lack FNOTE");
                refundComment = "理赔退款";
            } else {
                refundComment = returnOrder.getDescription();
            }
            Map<String, Object> FSETTLETYPEID = new HashMap<>();
            FSETTLETYPEID.put("FNumber", "QB");

            //使用银行卡
            Map<String, Object> FACCOUNTID = new HashMap<>();
            FACCOUNTID.put("FNumber", "QB");
            payOrderSettlement.setFACCOUNTID(FACCOUNTID);
            payOrderSettlement.setFSETTLETYPEID(FSETTLETYPEID);

            BigDecimal price = returnOrder.getReturnPrice().getApplyPrice().setScale(2, RoundingMode.DOWN);
            payOrderSettlement.setFRECTOTALAMOUNTFOR(price.toString());

            payOrderSettlement.setFNOTE(refundComment + "-" + lpRecordNo);
            freceivebillentry.add(payOrderSettlement);
            payOrder.setFRECEIVEBILLENTRY(freceivebillentry);
            //销售类型
            Map<String, String> fSaleType = new HashMap<>();
            if (Objects.nonNull(trade.getSaleType())) {
                fSaleType.put("FNumber", String.valueOf(trade.getSaleType().toValue()));
                payOrder.setFSaleType(fSaleType);
            } else {
                fSaleType.put("FNumber", String.valueOf(SaleType.WHOLESALE.toValue()));
                payOrder.setFSaleType(fSaleType);
            }

            //登录财务系统
            String loginToken = loginERP();
            if (StringUtils.isEmpty(loginToken)) {
                pushKingdeeRefundOrder.setInstructions("金蝶登录失败");
                pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
                return;
            }
            //推销售单
            pushPayOrderKingdee(lpRecordNo,trade.getId(),trade.getBuyer().getAccount(),price,loginToken,ClaimsApplyType.DEDUCTION_RECHARGE.getId().equals(returnOrder.getChaimApllyType()));

            //提交财务单
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("Model", payOrder);
            HttpCommonResult refundResult = HttpCommonUtil.postHeader(refundUrl, requestMap, loginToken);
            KingDeeResult kingDeeResult = JSONObject.parseObject(refundResult.getResultData(), KingDeeResult.class);
            log.info("PushKingdeeService.pushRefundOrderKingdeeForClaims result1:{}  code:{}", refundResult.getResultData(), refundResult.getResultCode());
            if (Objects.nonNull(kingDeeResult) && kingDeeResult.getCode().equals("0")) {
                pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.PUSHSUCCESS.toStatus());
            } else {
                pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
            }
            pushKingdeeRefundOrder.setInstructions(refundResult.getResultData());

        } catch (Exception e) {
            log.error("PushKingdeeService.pushRefundOrderKingdeeForClaims error:", e);
            String res = "金蝶推送失败";
            pushKingdeeRefundOrder.setInstructions(res);
            pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
        } finally {
            Integer number = tradePushKingdeeRefundRepository.selcetPushKingdeeRefundNumber(lpRecordNo);
            //没有创建，有就更新
            if (number == 0) {
                pushKingdeeRefundOrder.setOrderCode(trade.getId());
                pushKingdeeRefundOrder.setRefundCode(lpRecordNo);
                pushKingdeeRefundOrder.setPayType(PayWay.BALANCE.toValue());
                pushKingdeeRefundOrder.setCreateTime(LocalDateTime.now());
                pushKingdeeRefundOrder.setUpdateTime(LocalDateTime.now());
                pushKingdeeRefundOrder.setCustomerAccount(trade.getBuyer().getAccount());
                tradePushKingdeeRefundRepository.saveAndFlush(pushKingdeeRefundOrder);
            } else {
                pushKingdeeRefundOrder.setUpdateTime(LocalDateTime.now());
                pushKingdeeRefundOrder.setRefundCode(lpRecordNo);
                tradePushKingdeeRefundRepository.updatePushKingdeeRefundOrderState(pushKingdeeRefundOrder);
            }
        }
    }

    private boolean checkRefundOrderKingdee(NewPileTrade trade, NewPileReturnOrder returnOrder, TradePushKingdeeRefund pushKingdeeRefundOrder) {
        if (StringUtils.isEmpty(trade.getId())) {
            log.info("PushKingdeeService.pushRefundOrderKingdeeForClaims Lack Tid");
            pushKingdeeRefundOrder.setInstructions("Lack Tid");
            return false;
        }
        if (StringUtils.isEmpty(trade.getBuyer().getAccount())) {
            log.info("PushKingdeeService.pushRefundOrderKingdeeForClaims Lack FCustId");
            pushKingdeeRefundOrder.setInstructions("Lack FCustId");
            return false;
        }
        if (returnOrder.getReturnPrice().getApplyPrice() == null) {
            log.info("PushKingdeeService.pushRefundOrderKingdeeForClaims Lack FRECTOTALAMOUNTFOR");
            pushKingdeeRefundOrder.setInstructions("Lack FRECTOTALAMOUNTFOR");
            return false;
        }
        return true;
    }

    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public void pushRefundOrderKingdeeForCoin(CoinActivityPushKingdeeRequest request) {

        if (!kingdeeOpenState) {
            return;
        }

        String tid = request.getTid();
        log.info("pushRefundOrderKingdeeForCoin，tid:{}", tid);

        String sendNo = request.getSendNo();

        TradePushKingdeeRefund pushKingdeeRefundOrder = new TradePushKingdeeRefund();
        pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.CREATE.toStatus());
        try {
            if (!checkCoinOrderKingdee(tid, request.getBuyerAccount(), request.getApplyPrice(), pushKingdeeRefundOrder)) {
                pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.PARAMETERERROR.toStatus());
                return;
            }

            KingdeeRefundOrder payOrder = new KingdeeRefundOrder();
            if (StringUtils.isNotBlank(request.getPushDateStr())) {
                payOrder.setFDate(request.getPushDateStr());
            } else {
                payOrder.setFDate(DateUtil.nowDate());
            }
            payOrder.setOrderNumber(sendNo);
            payOrder.setFSaleNum(tid);

            Map<String, Object> FCustId = new HashMap<>();
            FCustId.put("FNumber", request.getBuyerAccount());
            payOrder.setFCustId(FCustId);

            Map<String, Object> FPAYORGID = new HashMap<>();
            FPAYORGID.put("FNumber", kingdeeOrganization);
            payOrder.setFPAYORGID(FPAYORGID);

            List<KingdeeRefundOrderSettlement> freceivebillentry = new ArrayList<>();
            KingdeeRefundOrderSettlement payOrderSettlement = new KingdeeRefundOrderSettlement();

            String refundComment = "金币赠送";

            Map<String, Object> FSETTLETYPEID = new HashMap<>();
            FSETTLETYPEID.put("FNumber", "QB");

            //使用银行卡
            Map<String, Object> FACCOUNTID = new HashMap<>();
            FACCOUNTID.put("FNumber", "QB");
            payOrderSettlement.setFACCOUNTID(FACCOUNTID);
            payOrderSettlement.setFSETTLETYPEID(FSETTLETYPEID);

            BigDecimal price = request.getApplyPrice();
            payOrderSettlement.setFRECTOTALAMOUNTFOR(price.toString());

            payOrderSettlement.setFNOTE(refundComment + "-" + sendNo);
            freceivebillentry.add(payOrderSettlement);
            payOrder.setFRECEIVEBILLENTRY(freceivebillentry);
            //销售类型
            Map<String, String> fSaleType = new HashMap<>();
            if (Objects.nonNull(request.getSaleType())) {
                fSaleType.put("FNumber", String.valueOf(request.getSaleType().toValue()));
                payOrder.setFSaleType(fSaleType);
            } else {
                fSaleType.put("FNumber", String.valueOf(SaleType.WHOLESALE.toValue()));
                payOrder.setFSaleType(fSaleType);
            }
            //登录财务系统
            String loginToken = loginERP();
            if (StringUtils.isEmpty(loginToken)) {
                pushKingdeeRefundOrder.setInstructions("金蝶登录失败");
                pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
                return;
            }

            //提交财务单
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("Model", payOrder);

            HttpCommonResult refundResult = HttpCommonUtil.postHeader(refundUrl, requestMap, loginToken);
            KingDeeResult kingDeeResult = JSONObject.parseObject(refundResult.getResultData(), KingDeeResult.class);
            log.info("PushKingdeeService.pushRefundOrderKingdeeForCoin result1:{}  code:{}", refundResult.getResultData(), refundResult.getResultCode());
            if (Objects.nonNull(kingDeeResult) && kingDeeResult.getCode().equals("0")) {
                pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.PUSHSUCCESS.toStatus());
            } else {
                pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
            }
            pushKingdeeRefundOrder.setInstructions(refundResult.getResultData());

        } catch (Exception e) {
            log.error("PushKingdeeService.pushRefundOrderKingdeeForCoin error:", e);
            String res = "金蝶推送失败";
            pushKingdeeRefundOrder.setInstructions(res);
            pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
        } finally {
            Integer number = tradePushKingdeeRefundRepository.selcetPushKingdeeRefundNumber(sendNo);
            //没有创建，有就更新
            if (number == 0) {
                pushKingdeeRefundOrder.setOrderCode(tid);
                pushKingdeeRefundOrder.setRefundCode(sendNo);
                pushKingdeeRefundOrder.setPayType(PayWay.BALANCE.toValue());
                pushKingdeeRefundOrder.setCreateTime(LocalDateTime.now());
                pushKingdeeRefundOrder.setUpdateTime(LocalDateTime.now());
                pushKingdeeRefundOrder.setCustomerAccount(request.getBuyerAccount());
                tradePushKingdeeRefundRepository.saveAndFlush(pushKingdeeRefundOrder);
            } else {
                pushKingdeeRefundOrder.setUpdateTime(LocalDateTime.now());
                pushKingdeeRefundOrder.setRefundCode(sendNo);
                tradePushKingdeeRefundRepository.updatePushKingdeeRefundOrderState(pushKingdeeRefundOrder);
            }
        }
    }

    private boolean checkCoinOrderKingdee(String tid, String account, BigDecimal applyPrice, TradePushKingdeeRefund pushKingdeeRefundOrder) {

        if (StringUtils.isEmpty(tid)) {
            log.info("PushKingdeeService.pushRefundOrderKingdeeForCoin Lack Tid");
            pushKingdeeRefundOrder.setInstructions("Lack Tid");
            return false;
        }
        if (StringUtils.isEmpty(account)) {
            log.info("PushKingdeeService.pushRefundOrderKingdeeForCoin Lack FCustId");
            pushKingdeeRefundOrder.setInstructions("Lack FCustId");
            return false;
        }
        if (applyPrice == null) {
            log.info("PushKingdeeService.pushRefundOrderKingdeeForCoin Lack FRECTOTALAMOUNTFOR");
            pushKingdeeRefundOrder.setInstructions("Lack FRECTOTALAMOUNTFOR");
            return false;
        }
        return true;
    }

    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public void pushOrderKingdeeForCoin(CoinActivityPushKingdeeRequest request) {

        if (!kingdeeOpenState) {
            return;
        }

        TradePushKingdeePayOrder tradePushKingdeePayOrder = new TradePushKingdeePayOrder();
        tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.CREATE.toStatus());
        try {
            KingdeePayOrder payOrder = new KingdeePayOrder();
            if (StringUtils.isNotBlank(request.getPushDateStr())) {
                payOrder.setFDate(request.getPushDateStr());
            } else {
                payOrder.setFDate(DateUtil.nowDate());
            }
            payOrder.setOrderNumber(request.getSendNo());
            Map FCustId = new HashMap();
            FCustId.put("FNumber", request.getBuyerAccount());
            payOrder.setFCustId(FCustId);
            Map FPAYORGID = new HashMap();
            FPAYORGID.put("FNumber", kingdeeOrganization);
            payOrder.setFPAYORGID(FPAYORGID);//收款组织
            //收款方式
            payOrder.setFColType("线上");
            List<KingdeePayOrderSettlement> freceivebillentry = new ArrayList<>();
            KingdeePayOrderSettlement payOrderSettlement = new KingdeePayOrderSettlement();
            Map FSETTLETYPEID = new HashMap();
            FSETTLETYPEID.put("FNumber", "QB");

            payOrderSettlement.setFSETTLETYPEID(FSETTLETYPEID);//结算方式
            payOrderSettlement.setFRECTOTALAMOUNTFOR(request.getApplyPrice().toString());

            //销售订单号
            payOrderSettlement.setF_ora_YDDH(request.getTid());

            Map FACCOUNTID = new HashMap();
            FACCOUNTID.put("FNumber", "103");
            payOrderSettlement.setFACCOUNTID(FACCOUNTID);
            //线下支付将收款银行信息推到金蝶
            freceivebillentry.add(payOrderSettlement);
            payOrder.setFRECEIVEBILLENTRY(freceivebillentry);

            //登录财务系统
            Map<String, Object> requestLogMap = new HashMap<>();
            requestLogMap.put("user", kingdeeUser);
            requestLogMap.put("pwd", kingdeePwd);
            String loginToken = kingdeeLoginUtils.userLoginKingdee(requestLogMap, loginUrl);
            if (StringUtils.isNotEmpty(loginToken)) {
                //提交财务单
                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("Model", payOrder);
                log.info("收款单推金蝶1111---实际推送内容-->" + JSONObject.toJSONString(payOrder));
                HttpCommonResult httpCommonResult = HttpCommonUtil.postHeader(payUrl, requestMap, loginToken);
                KingDeeResult kingDeeResult = JSONObject.parseObject(httpCommonResult.getResultData(), KingDeeResult.class);
                log.info("PushKingdeeService.pushOrderKingdeeForCoin result1:{} code:{}", httpCommonResult.getResultData(), kingDeeResult.getCode());
                if (Objects.nonNull(kingDeeResult) && kingDeeResult.getCode().equals("0")) {
                    tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.PUSHSUCCESS.toStatus());
                } else {
                    tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
                }
                tradePushKingdeePayOrder.setInstructions(httpCommonResult.getResultData());
            } else {
                log.error("PushKingdeeService.pushOrderKingdeeForCoin push kingdee error");
                String res = "金蝶登录失败";
                tradePushKingdeePayOrder.setInstructions(res);
                tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
            }
        } catch (Exception e) {
            log.error("PushKingdeeService.pushOrderKingdeeForCoin error:{}", e);
            String res = "金蝶推送失败";
            tradePushKingdeePayOrder.setInstructions(res);
            tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
        } finally {
            //查询支付记录中是否有支付单
            Integer number = tradePushKingdeePayRepository.selectPushKingdeePayOrderNumber(request.getSendNo());
            if (number == 0) {
                tradePushKingdeePayOrder.setPayCode(request.getSendNo());
                tradePushKingdeePayOrder.setOrderCode(request.getTid());
                tradePushKingdeePayOrder.setPayType(PayWay.UNIONPAY.toValue());
                tradePushKingdeePayOrder.setCreateTime(LocalDateTime.now());
                tradePushKingdeePayOrder.setUpdateTime(LocalDateTime.now());
                tradePushKingdeePayOrder.setPracticalPrice(request.getApplyPrice());
                tradePushKingdeePayRepository.saveAndFlush(tradePushKingdeePayOrder);
            } else {
                tradePushKingdeePayOrder.setUpdateTime(LocalDateTime.now());
                tradePushKingdeePayOrder.setPayCode(request.getSendNo());
                tradePushKingdeePayRepository.updatePushKingdeePayOrderState(tradePushKingdeePayOrder);
            }

        }
    }
}
