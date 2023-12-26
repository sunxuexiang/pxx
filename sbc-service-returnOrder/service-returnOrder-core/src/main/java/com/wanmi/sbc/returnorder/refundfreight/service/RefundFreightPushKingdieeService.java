package com.wanmi.sbc.returnorder.refundfreight.service;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.account.bean.enums.PayWay;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.HttpCommonResult;
import com.wanmi.sbc.common.util.HttpCommonUtil;
import com.wanmi.sbc.goods.bean.enums.SaleType;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.request.trade.TradeGetByIdRequest;
import com.wanmi.sbc.order.bean.vo.TradeDeliverVO;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import com.wanmi.sbc.pay.api.provider.CcbPayProvider;
import com.wanmi.sbc.pay.api.response.CcbRefundRecordResponse;
import com.wanmi.sbc.returnorder.enums.PushKingdeeStatusEnum;
import com.wanmi.sbc.returnorder.refundfreight.model.root.RefundFreightRecord;
import com.wanmi.sbc.returnorder.refundfreight.repository.RefundFreightRecordRepository;
import com.wanmi.sbc.returnorder.returnextra.model.root.RefundExtraRecord;
import com.wanmi.sbc.returnorder.returnextra.repository.RefundExtraRecordRepository;
import com.wanmi.sbc.returnorder.returnorder.model.root.TradePushKingdeeRefund;
import com.wanmi.sbc.returnorder.returnorder.repository.TradePushKingdeeRefundRepository;
import com.wanmi.sbc.returnorder.trade.model.entity.value.KingDeeResult;
import com.wanmi.sbc.returnorder.trade.model.entity.value.KingdeeRefundOrder;
import com.wanmi.sbc.returnorder.trade.model.entity.value.KingdeeRefundOrderSettlement;
import com.wanmi.sbc.returnorder.util.KingdeeLoginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/12/4 16:12
 */
@Service
@Slf4j
public class RefundFreightPushKingdieeService {

    @Autowired
    private RefundFreightRecordRepository refundFreightRecordRepository;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private CcbPayProvider ccbPayProvider;

    @Autowired
    private TradePushKingdeeRefundRepository tradePushKingdeeRefundRepository;

    @Value("${kingdee.organization}")
    private String kingdeeOrganization;

    @Value("${kingdee.ccb}")
    private String kingdeeCcbpay;

    @Value("${kingdee.user}")
    private String kingdeeUser;

    @Value("${kingdee.pwd}")
    private String kingdeePwd;

    @Value("${kingdee.login.url}")
    private String loginUrl;

    @Value("${kingdee.Return.url}")
    private String returnUrl;

    @Value("${kingdee.Refurn.url}")
    private String refurnUrl;

    @Autowired
    private KingdeeLoginUtils kingdeeLoginUtils;

    @Autowired
    private RefundExtraRecordRepository refundExtraRecordRepository;

    @Async
    public void refundFreightPushKingdiee(String tid) {
        log.info("手动退运费推送ERP开始，订单号：{}", tid);
        RefundFreightRecord record = refundFreightRecordRepository.findByTid(tid);
        if (Objects.isNull(record)) {
            log.info("手动退运费记录不存在,订单ID：{}", tid);
            return;
        }
        if (!Objects.equals(1, record.getRefundStatus())) {
            log.info("手动退运费,退款状态未成功,订单ID：{}", tid);
            return;
        }
        TradeVO tradeVO = tradeQueryProvider.getOrderById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
        if (Objects.isNull(tradeVO)) {
            log.info("手动退运费,订单不存在,订单ID：{}", tid);
            return;
        }
        CcbRefundRecordResponse refundRecord = ccbPayProvider.queryCcbRefundRecordByRid(record.getRid()).getContext();
        if (Objects.isNull(refundRecord)) {
            log.info("手动退运费,建行退款记录不存在,订单ID：{}", tid);
            return;
        }
        BigDecimal commissionPrice = refundRecord.getCommissionPrice();
        BigDecimal freightCommissionPrice = refundRecord.getFreightCommissionPrice();
        String rid = refundRecord.getRid();
        String tradeId = refundRecord.getTid();
        if (Objects.nonNull(commissionPrice) && commissionPrice.compareTo(BigDecimal.ZERO) > 0) {
            String companyCodeNew = tradeVO.getSupplier().getSupplierCodeNew();
            if (StringUtils.isNotBlank(companyCodeNew)) {
                log.info("手动退运费，佣金。退单号：{}，商家编码：{}", rid, companyCodeNew);
                this.refundFreightPushKingdieeExecute(commissionPrice, rid, tradeId, companyCodeNew, "YJ");
            }

        }

        if (Objects.nonNull(freightCommissionPrice) && freightCommissionPrice.compareTo(BigDecimal.ZERO) > 0) {
            //  承运商退款佣金推送金蝶
            TradeDeliverVO deliverVO = tradeVO.getTradeDelivers().stream().findFirst().orElse(null);
            if (Objects.nonNull(deliverVO) && Objects.nonNull(deliverVO.getLogistics().getLogisticCompanyId())) {
                String logisticCompanyId = deliverVO.getLogistics().getLogisticCompanyId();
                log.info("手动退运费，承运商佣金。退单号：{}，商家编码：{}", rid, logisticCompanyId);
                this.refundFreightPushKingdieeExecute(freightCommissionPrice, rid, tradeId, logisticCompanyId, "YFYJ");
            }
        }
    }

    @Async
    public void refundExtraPushKingdiee(String extraId) {
        log.info("退运费加收推送ERP开始，订单号：{}", extraId);
        RefundExtraRecord record = refundExtraRecordRepository.findByExtraId(extraId);
        if (Objects.isNull(record)) {
            log.info("退运费加收记录不存在,订单ID：{}", extraId);
            return;
        }
        if (!Objects.equals(1, record.getRefundStatus())) {
            log.info("退运费加收,退款状态未成功,订单ID：{}", extraId);
            return;
        }
        TradeVO tradeVO = tradeQueryProvider.getOrderById(TradeGetByIdRequest.builder().tid(extraId).build()).getContext().getTradeVO();
        if (Objects.isNull(tradeVO)) {
            log.info("退运费加收,订单不存在,订单ID：{}", extraId);
            return;
        }
        CcbRefundRecordResponse refundRecord = ccbPayProvider.queryCcbRefundRecordByRid("E" + extraId).getContext();
        if (Objects.isNull(refundRecord)) {
            log.info("退运费加收,建行退款记录不存在,订单ID：{}", extraId);
            return;
        }
        BigDecimal commissionPrice = refundRecord.getCommissionPrice();
        BigDecimal freightCommissionPrice = refundRecord.getFreightCommissionPrice();
        String rid = refundRecord.getRid();
        String tradeId = refundRecord.getTid();
        if (Objects.nonNull(commissionPrice) && commissionPrice.compareTo(BigDecimal.ZERO) > 0) {
            String companyCodeNew = tradeVO.getSupplier().getSupplierCodeNew();
            if (StringUtils.isNotBlank(companyCodeNew)) {
                log.info("退运费加收，佣金。退单号：{}，商家编码：{}", rid, companyCodeNew);
                this.refundFreightPushKingdieeExecute(commissionPrice, rid, tradeId, companyCodeNew, "YJ");
            }

        }

        if (Objects.nonNull(freightCommissionPrice) && freightCommissionPrice.compareTo(BigDecimal.ZERO) > 0) {
            //  承运商退款佣金推送金蝶
            TradeDeliverVO deliverVO = tradeVO.getTradeDelivers().stream().findFirst().orElse(null);
            if (Objects.nonNull(deliverVO) && Objects.nonNull(deliverVO.getLogistics().getLogisticCompanyId())) {
                String logisticCompanyId = deliverVO.getLogistics().getLogisticCompanyId();
                log.info("退运费加收，承运商佣金。退单号：{}，商家编码：{}", rid, logisticCompanyId);
                this.refundFreightPushKingdieeExecute(freightCommissionPrice, rid, tradeId, logisticCompanyId, "YFYJ");
            }
        }
    }

    public void refundFreightPushKingdieeExecute(BigDecimal commission, String returnOrderCode, String tid, String account, String suffix) {
        returnOrderCode = returnOrderCode + suffix;
        tid = tid + suffix;
        log.info("退运费推送金蝶 单号:{},佣金:{}", returnOrderCode, commission);
        Integer number = tradePushKingdeeRefundRepository.selcetPushKingdeeRefundNumber(returnOrderCode);
        TradePushKingdeeRefund pushKingdeeRefundOrder = new TradePushKingdeeRefund();
        pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.CREATE.toStatus());
        try {
            KingdeeRefundOrder payOrder = new KingdeeRefundOrder();
            payOrder.setFDate(DateUtil.nowDate());
            payOrder.setOrderNumber(returnOrderCode);
            payOrder.setFSaleNum(tid);
            Map FCustId = new HashMap();
            FCustId.put("FNumber", account);
            payOrder.setFCustId(FCustId);
            Map FPAYORGID = new HashMap();
            FPAYORGID.put("FNumber", kingdeeOrganization);
            payOrder.setFPAYORGID(FPAYORGID);
            List<KingdeeRefundOrderSettlement> freceivebillentry = new ArrayList<>();
            KingdeeRefundOrderSettlement payOrderSettlement = new KingdeeRefundOrderSettlement();
            String refundComment = "手动退运费";
            Map FSETTLETYPEID = new HashMap();

            FSETTLETYPEID.put("FNumber", PayWay.CCB.toValue());

            // 使用银行卡
            Map FACCOUNTID = new HashMap();
            FACCOUNTID.put("FNumber", kingdeeCcbpay);

            payOrderSettlement.setFACCOUNTID(FACCOUNTID);
            payOrderSettlement.setFSETTLETYPEID(FSETTLETYPEID);

            payOrderSettlement.setFRECTOTALAMOUNTFOR(commission.toString());

            payOrderSettlement.setFNOTE(refundComment + "-" + returnOrderCode);
            freceivebillentry.add(payOrderSettlement);
            payOrder.setFRECEIVEBILLENTRY(freceivebillentry);
            // 销售类型
            Map fSaleType = new HashMap();
            fSaleType.put("FNumber", String.valueOf(SaleType.WHOLESALE.toValue()));
            payOrder.setFSaleType(fSaleType);

            // 登录财务系统
            Map<String, Object> requestLogMap = new HashMap<>();
            requestLogMap.put("user", kingdeeUser);
            requestLogMap.put("pwd", kingdeePwd);
            String loginToken = kingdeeLoginUtils.userLoginKingdee(requestLogMap, loginUrl);
            if (StringUtils.isNotEmpty(loginToken)) {
                // 提交财务单
                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("Model", payOrder);
                HttpCommonResult result1 = HttpCommonUtil.postHeader(refurnUrl, requestMap, loginToken);
                KingDeeResult kingDeeResult = JSONObject.parseObject(result1.getResultData(), KingDeeResult.class);
                log.info("RefundFreightPushKingdieeService.refundFreightPushKingdieeExecute result:{}  code:{}", result1.getResultData(), result1.getResultCode());
                if (Objects.nonNull(kingDeeResult) && kingDeeResult.getCode().equals("0")) {
                    pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.PUSHSUCCESS.toStatus());
                } else {
                    pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
                }
                pushKingdeeRefundOrder.setInstructions(result1.getResultData());
            } else {
                String res = "金蝶登录失败";
                pushKingdeeRefundOrder.setInstructions(res);
                pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
            }
        } catch (Exception e) {
            log.error("RefundFreightPushKingdieeService.refundFreightPushKingdieeExecute error:", e);
            String res = "金蝶推送失败";
            pushKingdeeRefundOrder.setInstructions(res);
            pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
        } finally {
            // 没有创建，有就更新
            if (number == 0) {
                pushKingdeeRefundOrder.setOrderCode(tid);
                pushKingdeeRefundOrder.setRefundCode(returnOrderCode);
                pushKingdeeRefundOrder.setPayType(PayWay.CCB.toValue());
                pushKingdeeRefundOrder.setCreateTime(LocalDateTime.now());
                pushKingdeeRefundOrder.setUpdateTime(LocalDateTime.now());
                pushKingdeeRefundOrder.setCustomerAccount(account);
                tradePushKingdeeRefundRepository.saveAndFlush(pushKingdeeRefundOrder);
            } else {
                pushKingdeeRefundOrder.setUpdateTime(LocalDateTime.now());
                pushKingdeeRefundOrder.setRefundCode(returnOrderCode);
                tradePushKingdeeRefundRepository.updatePushKingdeeRefundOrderState(pushKingdeeRefundOrder);
            }
        }
    }

}
