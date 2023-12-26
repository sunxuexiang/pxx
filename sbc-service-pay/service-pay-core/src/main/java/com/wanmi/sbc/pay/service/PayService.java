package com.wanmi.sbc.pay.service;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.chinapay.secss.SecssUtil;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.account.api.provider.wallet.CustomerWalletProvider;
import com.wanmi.sbc.account.bean.enums.WalletDetailsType;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.ResultCode;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.GeneratorService;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.pay.api.request.*;
import com.wanmi.sbc.pay.api.response.*;
import com.wanmi.sbc.pay.bean.enums.*;
import com.wanmi.sbc.pay.gateway.GatewayProxy;
import com.wanmi.sbc.pay.model.entity.PayRecordResult;
import com.wanmi.sbc.pay.model.entity.PayResult;
import com.wanmi.sbc.pay.model.entity.RefundRecordResult;
import com.wanmi.sbc.pay.model.entity.RefundResult;
import com.wanmi.sbc.pay.model.root.*;
import com.wanmi.sbc.pay.repository.*;
import com.wanmi.sbc.pay.unionpay.acp.sdk.AcpService;
import com.wanmi.sbc.pay.unionpay.acp.sdk.SDKConfig;
import com.wanmi.sbc.pay.utils.GeneratorUtils;
import com.wanmi.sbc.pay.utils.PayValidates;
import com.wanmi.sbc.pay.utils.SaasServiceUtils;
import com.wanmi.sbc.pay.weixinpaysdk.WXPayConstants;
import com.wanmi.sbc.pay.weixinpaysdk.WXPayUtil;
import com.wanmi.sbc.wallet.api.provider.wallet.WalletMerchantProvider;
import com.wanmi.sbc.wallet.api.request.wallet.CustomerWalletGiveRequest;
import com.wanmi.sbc.wallet.bean.enums.WalletRecordTradeType;
import com.wanmi.sbc.wallet.bean.vo.WalletRecordVO;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * <p>支付交易service</p>
 * Created by of628-wenzhi on 2017-08-02-下午5:44.
 */
@Service
@Validated
@Slf4j
public class PayService {

    private static final String WXPAYATYPE = "PC/H5/JSAPI"; //微信支付类型--为PC/H5/JSAPI，对应调用参数对应公众平台参数

    private static final String WXPAYAPPTYPE = "APP"; //微信支付类型--为app，对应调用参数对应开放平台参数

    @Resource
    private GatewayProxy proxy;

    @Resource
    private ChannelItemRepository channelItemRepository;

    @Resource
    private TradeRecordRepository recordRepository;

    @Resource
    private GatewayRepository gatewayRepository;

    @Resource
    private GatewayConfigRepository gatewayConfigRepository;

    @Autowired
    private AlipayService alipayService;

    @Autowired
    private WxPayService wxPayService;

    @Autowired
    private SaasServiceUtils saasServiceUtils;

    @Autowired
    private CmbService cmbService;

    @Autowired
    private CupsService cupsService;

    @Autowired
    private PayDataService payDataService;

    @Value("${china.pay.security.path}")
    private String url;

    @Value("${china.pay.request.front.url}")
    private String requestFrontUrl;

    @Value("${new.version}")
    private String newVersion;

    @Value("${union.refund.url}")
    private String unionRefundUrl;

    @Autowired
    private CustomerWalletProvider customerWalletProvider;

    @Autowired
    private CcbService ccbService;

    @Autowired
    private CcbRefundRetryService ccbRefundRetryService;

    @Autowired
    private WalletMerchantProvider walletMerchantProvider;

    @Autowired
    private CcbPayRecordRepository ccbPayRecordRepository;

    /**
     * 获取支付对象，这里返回到的支付对象实际为支付凭证，由前端获取后通过JS请求第三方支付
     *
     * @param request
     */
    @Transactional(noRollbackFor = SbcRuntimeException.class)
    public Object pay(@Valid PayExtraRequest request) {

        //获取支付渠道项
        PayChannelItem item = getPayChannelItem(request.getChannelItemId(),request.getStoreId());
        if (item.getTerminal() != request.getTerminal()) {
            throw new SbcRuntimeException("K-100202");
        }
        //是否重复支付
        PayTradeRecord record = recordRepository.findByBusinessId(request.getBusinessId());
        if (!Objects.isNull(record)) {
            //如果重复支付，判断状态，已成功状态则做异常提示
            if (record.getStatus() == TradeStatus.SUCCEED) {
                throw new SbcRuntimeException("K-100203");
            } else if (record.getStatus() == TradeStatus.PROCESSING && !Objects.isNull(record.getChargeId())) {
                //未支付状态，跟踪支付结果
                PayRecordResult result = queryPayResult(record);
                if (result.getRecord().getStatus() == TradeStatus.SUCCEED) {
                    //更新记录
                    recordRepository.updateTradeStatusAndPracticalPriceAndFinishTime(
                            result.getRecord().getId(),
                            result.getRecord().getStatus(),
                            result.getRecord().getPracticalPrice(),
                            result.getRecord().getFinishTime()
                    );
                    throw new SbcRuntimeException("K-100203");
                } else if (result.getRecord().getStatus() == TradeStatus.PROCESSING) {
                    //30秒内阻止重复创建
                    Duration duration1 = Duration.between(LocalDateTime.now(), result.getRecord().getCreateTime());
                    if (duration1.toMillis() > -30000) {
                        throw new SbcRuntimeException("K-100207");
                    }
                    if (!Objects.isNull(result.getObject())
                            && Objects.equals(result.getRecord().getChannelItemId(), request.getChannelItemId())) {
                        Duration duration2 = Duration.between(LocalDateTime.now(), result.getTimeExpire());
                        //相同单号和支付渠道下的未支付订单，金额如果不一致则重新创建支付对象，一致且支付凭证未失效，则返回已创建的支付对象
                        if (request.getAmount().compareTo(record.getApplyPrice()) == 0 && duration2.toMillis() > 0) {
                            return result.getObject();
                        }
                    }
                }
            }
        }

        PayValidates.verifyGateway(item.getGateway());

        //调用网关支付
        PayResult result = proxy.pay(request, item);
        //获取支付对象,存入记录
        savePayRecord(request, result);
        return result.getData();
    }

    /**
     * 根据订单号查询支付结果
     *
     * @param tid 业务订单号
     * @return 支付结果
     */
    @Transactional
    public TradeStatus queryPayResult(String tid) {
        PayTradeRecord record = recordRepository.findByBusinessId(tid);
        if (!Objects.isNull(record)) {
            //如果重复支付，判断状态
            if (record.getStatus() == TradeStatus.SUCCEED) {
                return TradeStatus.SUCCEED;
            } else if (record.getStatus() == TradeStatus.PROCESSING && !Objects.isNull(record.getChargeId())) {
                //未支付状态，跟踪支付结果
                PayRecordResult result = queryPayResult(record);
                if (result.getRecord().getStatus() == TradeStatus.SUCCEED) {
                    //更新记录
                    recordRepository.updateTradeStatusAndPracticalPriceAndFinishTime(
                            result.getRecord().getId(),
                            result.getRecord().getStatus(),
                            result.getRecord().getPracticalPrice(),
                            result.getRecord().getFinishTime()
                    );
                    return TradeStatus.SUCCEED;
                } else if (result.getRecord().getStatus() == TradeStatus.PROCESSING
                        && !Objects.isNull(result.getObject())) {
                    return TradeStatus.PROCESSING;
                }
            }
        }
        return null;
    }

    /**
     * 退款
     *
     * @param request
     */
    @LcnTransaction
    @Transactional(noRollbackFor = SbcRuntimeException.class)
    public Object pileRefund(@Valid RefundRequest request) {
        //重复退款校验
        TradeStatus status = queryRefundResult(request.getRefundBusinessId(), request.getBusinessId());
        if (!Objects.isNull(status)) {
            if (status == TradeStatus.SUCCEED) {
                throw new SbcRuntimeException("K-100104");
            } else if (status == TradeStatus.PROCESSING) {
                throw new SbcRuntimeException("K-100105");
            }
        }

        //未退款或退款失败的退单，调用网关执行退款操作
        PayTradeRecord payRecord = recordRepository.findTopByBusinessIdAndStatus(request.getBusinessId(), TradeStatus.SUCCEED);

        Long storeId = saasServiceUtils.getStoreIdWithDefault(request.getStoreId());
        request.setStoreId(storeId);
        PayChannelItem item = getPayChannelItem(payRecord.getChannelItemId(),storeId);
        if (payRecord.getChannelItemId().equals(11L)) {
            //银联企业支付退款
            PayValidates.verifyGateway(item.getGateway());
            PayTradeRecord record = saveRefundRecord(request, item);
//            payRecord.setBusinessId(record.getBusinessId());
            record.setTradeNo(payRecord.getTradeNo());
            PayGatewayConfig gatewayConfig = gatewayConfigRepository.queryConfigByNameAndStoreId(PayGatewayEnum.UNIONB2B, storeId);
//            Map<String, String> resultMap = unionRefund(record, gatewayConfig);
            Map<String, String> resultMap = newUnionRefund(record, gatewayConfig,payRecord);
            log.info(">>>>>>>>>>>>>>>>>>respCode:" + resultMap.get("respCode") + "respMsg:" + resultMap.get("respMsg"));
            if ("0000".equals(resultMap.get("respCode"))) {
//                record.setTradeNo(resultMap.get("orderId"));
//                recordRepository.save(record);
            } else {
                //提交退款申请失败
                throw new SbcRuntimeException("K-100105");
            }
            return resultMap;
        } else if (payRecord.getChannelItemId().equals(14L) || payRecord.getChannelItemId().equals(15L) ||
                payRecord.getChannelItemId().equals(16L)) {
            //微信支付退款--PC、H5、JSAPI支付对应付退款
            return wxPayPileRefundForPcH5Jsapi(item, request, payRecord);
        } else if (payRecord.getChannelItemId().equals(20L)) {
            //微信支付--app支付退款
            return wxPayPileRefundForApp(item, request, payRecord);
        } else if (payRecord.getChannelItemId() == 17L || payRecord.getChannelItemId() == 18L || payRecord.getChannelItemId() == 19L) {
            //支付宝退款
            PayTradeRecord data = saveRefundRecord(request, item);
            AliPayRefundRequest aliPayRefundRequest = KsBeanUtil.convert(request, AliPayRefundRequest.class);
            aliPayRefundRequest.setAppid(item.getGateway().getConfig().getAppId());
            aliPayRefundRequest.setAppPrivateKey(item.getGateway().getConfig().getPrivateKey());
            aliPayRefundRequest.setAliPayPublicKey(item.getGateway().getConfig().getPublicKey());
            AlipayTradeRefundResponse refundResponse = alipayService.tradeRefund(aliPayRefundRequest);
            AliPayRefundResponse aliPayRefundResponse = new AliPayRefundResponse();
            aliPayRefundResponse.setPayType("ALIPAY");
            aliPayRefundResponse.setAlipayTradeRefundResponse(refundResponse);

            if (refundResponse.isSuccess()) {
                //更新记录
                data.setChargeId(request.getRefundBusinessId());
                data.setTradeNo(refundResponse.getTradeNo());
                data.setFinishTime(LocalDateTime.now());
                data.setStatus(TradeStatus.SUCCEED);
                data.setCallbackTime(LocalDateTime.now());
                data.setPracticalPrice(new BigDecimal(refundResponse.getRefundFee()));
                recordRepository.save(data);
            } else {
                throw new SbcRuntimeException("K-100211", new Object[]{refundResponse.getSubMsg()});
            }
            return aliPayRefundResponse;
        } else if (payRecord.getChannelItemId() == 21L || payRecord.getChannelItemId() == 22L || payRecord.getChannelItemId() == 23L) {
            //余额支付退款操作
            return balanceRefund(item, request);
        } else {
            request.setPayObjectId(payRecord.getChargeId());
            PayValidates.verifyGateway(item.getGateway());
            PayTradeRecord data = saveRefundRecord(request, item);
            RefundResult result = proxy.refund(request, item);
            try {
                //更新记录
                data.setChargeId(result.getRefundObjectId());
                data.setTradeNo(result.getTradeNo());
                recordRepository.save(data);
            } catch (Exception e) {
                log.error("After calling the gateway refund operation, the update record fails，" +
                                "request={}," +
                                "result={}",
                        request,
                        result,
                        e
                );
            }
            return result.getData();
        }
    }

    /**
     * 退款
     *
     * @param request
     */
    private Object refundNew(@Valid RefundRequest request) {
        //重复退款校验
        TradeStatus status = queryRefundResult(request.getRefundBusinessId(), request.getBusinessId());
        if (!Objects.isNull(status)) {
            if (status == TradeStatus.SUCCEED) {
                throw new SbcRuntimeException("K-100104");
            } else if (status == TradeStatus.PROCESSING) {
                throw new SbcRuntimeException("K-100105");
            }
        }

        //未退款或退款失败的退单，调用网关执行退款操作
        PayTradeRecord payRecord = recordRepository.findTopByBusinessIdAndStatus(request.getBusinessId(), TradeStatus.SUCCEED);
        if (Objects.isNull(payRecord)) {
            throw new SbcRuntimeException("未找到订单支付成功记录：订单号=" + request.getBusinessId());
        }
        request.setTotalPrice(payRecord.getApplyPrice());

        Long storeId = saasServiceUtils.getStoreIdWithDefault(request.getStoreId());
        request.setStoreId(storeId);
        PayChannelItem item = getPayChannelItem(payRecord.getChannelItemId(),storeId);

        if (payRecord.getChannelItemId().equals(11L)) {
            //银联企业支付退款
            PayValidates.verifyGateway(item.getGateway());
            PayTradeRecord record = saveRefundRecord(request, item);
//            payRecord.setBusinessId(record.getBusinessId());
            record.setTradeNo(payRecord.getTradeNo());
            PayGatewayConfig gatewayConfig = gatewayConfigRepository.queryConfigByNameAndStoreId(PayGatewayEnum.UNIONB2B, storeId);
//            Map<String, String> resultMap = unionRefund(record, gatewayConfig);
            Map<String, String> resultMap = newUnionRefund(record, gatewayConfig,payRecord);
            log.info(">>>>>>>>>>>>>>>>>>respCode:" + resultMap.get("respCode") + "respMsg:" + resultMap.get("respMsg"));
            if ("0000".equals(resultMap.get("respCode"))) {
//                record.setTradeNo(resultMap.get("orderId"));
//                recordRepository.save(record);
            } else {
                //提交退款申请失败
                throw new SbcRuntimeException("K-100105");
            }
            return resultMap;
        }else if (payRecord.getChannelItemId().equals(14L) || payRecord.getChannelItemId().equals(15L) ||
                payRecord.getChannelItemId().equals(16L)) {
            //微信支付退款--PC、H5、JSAPI支付对应付退款
            return wxPayRefundForPcH5Jsapi(item, request, payRecord);
        } else if (payRecord.getChannelItemId().equals(20L)) {
            //微信支付--app支付退款
            return wxPayRefundForApp(item, request, payRecord);
        } else if (payRecord.getChannelItemId() == 17L || payRecord.getChannelItemId() == 18L || payRecord.getChannelItemId() == 19L) {
            //支付宝退款
            PayTradeRecord data = saveRefundRecord(request, item);
            AliPayRefundRequest aliPayRefundRequest = KsBeanUtil.convert(request, AliPayRefundRequest.class);
            aliPayRefundRequest.setAppid(item.getGateway().getConfig().getAppId());
            aliPayRefundRequest.setAppPrivateKey(item.getGateway().getConfig().getPrivateKey());
            aliPayRefundRequest.setAliPayPublicKey(item.getGateway().getConfig().getPublicKey());
            AlipayTradeRefundResponse refundResponse = alipayService.tradeRefund(aliPayRefundRequest);
            AliPayRefundResponse aliPayRefundResponse = new AliPayRefundResponse();
            aliPayRefundResponse.setPayType("ALIPAY");
            aliPayRefundResponse.setAlipayTradeRefundResponse(refundResponse);

            if (refundResponse.isSuccess()) {
                //更新记录
                data.setChargeId(request.getRefundBusinessId());
                data.setTradeNo(refundResponse.getTradeNo());
                data.setFinishTime(LocalDateTime.now());
                data.setStatus(TradeStatus.SUCCEED);
                data.setCallbackTime(LocalDateTime.now());
                data.setPracticalPrice(new BigDecimal(refundResponse.getRefundFee()));
                recordRepository.save(data);
            } else {
                throw new SbcRuntimeException("K-100211", new Object[]{refundResponse.getSubMsg()});
            }
            return aliPayRefundResponse;
        } else if (payRecord.getChannelItemId() == 21L || payRecord.getChannelItemId() == 22L || payRecord.getChannelItemId() == 23L) {
            //余额支付退款操作
            return balanceRefund(item, request);
        }else if (payRecord.getChannelItemId() == 28L) {
            return cmbPayRefundForApp(item, request, payRecord);
        }else if (payRecord.getChannelItemId() == 29L || payRecord.getChannelItemId() == 30L) {
            return cupsPayRefundForApp(item, request, payRecord);
        }else if (payRecord.getChannelItemId() == 32L) {
            return ccbPayRefundForApp(item, request, payRecord);
        }else {
            return balanceRefund(item, request);
        }
    }


    /**
     * 退款
     *
     * @param request
     */
    @LcnTransaction
    @Transactional(noRollbackFor = SbcRuntimeException.class)
    public Object refund(@Valid RefundRequest request) {
        //重复退款校验
        TradeStatus status = queryRefundResult(request.getRefundBusinessId(), request.getBusinessId());
        if (!Objects.isNull(status)) {
            if (status == TradeStatus.SUCCEED) {
                throw new SbcRuntimeException("K-100104");
            } else if (status == TradeStatus.PROCESSING) {
                throw new SbcRuntimeException("K-100105");
            }
        }

        //未退款或退款失败的退单，调用网关执行退款操作
        PayTradeRecord payRecord = recordRepository.findTopByBusinessIdAndStatus(request.getBusinessId(), TradeStatus.SUCCEED);

        Long storeId = saasServiceUtils.getStoreIdWithDefault(request.getStoreId());
        request.setStoreId(storeId);
        PayChannelItem item = getPayChannelItem(payRecord.getChannelItemId(),storeId);

//        //好友代付，原路返回(目前只有微信支付)
//        if(payRecord.getChannelItemId().equals(14L) || payRecord.getChannelItemId().equals(15L) ||
//                payRecord.getChannelItemId().equals(16L)){
////        if (request.getOrderSource() != null && request.getOrderSource().intValue() == 1) {
////            if (payRecord.getChannelItemId().equals(14L) || payRecord.getChannelItemId().equals(15L) ||
////                    payRecord.getChannelItemId().equals(16L)) {
//                //微信支付退款--PC、H5、JSAPI支付对应付退款
//                return wxPayRefundForPcH5Jsapi(item, request, payRecord);
////            }
////            else if (payRecord.getChannelItemId().equals(20L)) {
////                //微信支付--app支付退款
////                return wxPayRefundForApp(item, request, payRecord);
////            }else {
////                return balanceRefund(item, request);
////            }
//        }else{
//            return balanceRefund(item, request);
//        }

        //不支持原路返回
        if(!request.isRefund()){
            return balanceRefund(item, request);
        }

        if (payRecord.getChannelItemId().equals(11L)) {
            //银联企业支付退款
            PayValidates.verifyGateway(item.getGateway());
            PayTradeRecord record = saveRefundRecord(request, item);
//            payRecord.setBusinessId(record.getBusinessId());
            record.setTradeNo(payRecord.getTradeNo());
            PayGatewayConfig gatewayConfig = gatewayConfigRepository.queryConfigByNameAndStoreId(PayGatewayEnum.UNIONB2B, storeId);
//            Map<String, String> resultMap = unionRefund(record, gatewayConfig);
            Map<String, String> resultMap = newUnionRefund(record, gatewayConfig,payRecord);
            log.info(">>>>>>>>>>>>>>>>>>respCode:" + resultMap.get("respCode") + "respMsg:" + resultMap.get("respMsg"));
            if ("0000".equals(resultMap.get("respCode"))) {
//                record.setTradeNo(resultMap.get("orderId"));
//                recordRepository.save(record);
            } else {
                //提交退款申请失败
                throw new SbcRuntimeException("K-100105");
            }
            return resultMap;
        }else if (payRecord.getChannelItemId().equals(14L) || payRecord.getChannelItemId().equals(15L) ||
                payRecord.getChannelItemId().equals(16L)) {
            //微信支付退款--PC、H5、JSAPI支付对应付退款
            return wxPayRefundForPcH5Jsapi(item, request, payRecord);
        } else if (payRecord.getChannelItemId().equals(20L)) {
            //微信支付--app支付退款
            return wxPayRefundForApp(item, request, payRecord);
        } else if (payRecord.getChannelItemId() == 17L || payRecord.getChannelItemId() == 18L || payRecord.getChannelItemId() == 19L) {
            //支付宝退款
            PayTradeRecord data = saveRefundRecord(request, item);
            AliPayRefundRequest aliPayRefundRequest = KsBeanUtil.convert(request, AliPayRefundRequest.class);
            aliPayRefundRequest.setAppid(item.getGateway().getConfig().getAppId());
            aliPayRefundRequest.setAppPrivateKey(item.getGateway().getConfig().getPrivateKey());
            aliPayRefundRequest.setAliPayPublicKey(item.getGateway().getConfig().getPublicKey());
            AlipayTradeRefundResponse refundResponse = alipayService.tradeRefund(aliPayRefundRequest);
            AliPayRefundResponse aliPayRefundResponse = new AliPayRefundResponse();
            aliPayRefundResponse.setPayType("ALIPAY");
            aliPayRefundResponse.setAlipayTradeRefundResponse(refundResponse);

            if (refundResponse.isSuccess()) {
                //更新记录
                data.setChargeId(request.getRefundBusinessId());
                data.setTradeNo(refundResponse.getTradeNo());
                data.setFinishTime(LocalDateTime.now());
                data.setStatus(TradeStatus.SUCCEED);
                data.setCallbackTime(LocalDateTime.now());
                data.setPracticalPrice(new BigDecimal(refundResponse.getRefundFee()));
                recordRepository.save(data);
            } else {
                throw new SbcRuntimeException("K-100211", new Object[]{refundResponse.getSubMsg()});
            }
            return aliPayRefundResponse;
        } else if (payRecord.getChannelItemId() == 21L || payRecord.getChannelItemId() == 22L || payRecord.getChannelItemId() == 23L) {
            //余额支付退款操作
            return balanceRefund(item, request);
        }else if (payRecord.getChannelItemId() == 28L) {
            return cmbPayRefundForApp(item, request, payRecord);
        }else if (payRecord.getChannelItemId() == 29L || payRecord.getChannelItemId() == 30L) {
            return cupsPayRefundForApp(item, request, payRecord);
        }else {
//            request.setPayObjectId(payRecord.getChargeId());
//            PayValidates.verifyGateway(item.getGateway());
//            PayTradeRecord data = saveRefundRecord(request, item);
//            RefundResult result = proxy.refund(request, item);
//            try {
//                //更新记录
//                data.setChargeId(result.getRefundObjectId());
//                data.setTradeNo(result.getTradeNo());
//                recordRepository.save(data);
//            } catch (Exception e) {
//                log.error("After calling the gateway refund operation, the update record fails，" +
//                                "request={}," +
//                                "result={}",
//                        request,
//                        result,
//                        e
//                );
//            }
//            return result.getData();
            return balanceRefund(item, request);
        }
    }

    /**
     * @return com.wanmi.sbc.pay.api.response.WxPayRefundResponse
     * @Author lvzhenwei
     * @Description 余额支付订单在线退款
     * @Date 15:59 2019/7/11
     * @Param [item, request, payRecord]
     **/
    private BalanceRefundResponse balanceRefund(PayChannelItem item, RefundRequest request) {
        PayTradeRecord data = saveRefundRecord(request, item);
        //更新记录
        data.setChargeId(request.getRefundBusinessId());
        data.setFinishTime(LocalDateTime.now());
        data.setStatus(TradeStatus.SUCCEED);
        data.setCallbackTime(LocalDateTime.now());
        data.setPracticalPrice(request.getAmount());
        recordRepository.save(data);
        BalanceRefundResponse balanceRefundResponse = new BalanceRefundResponse();
        balanceRefundResponse.setPayType(PayGatewayEnum.BALANCE.name());
        return balanceRefundResponse;
    }

    /**
     * 微信支付--PC、H5、JSAPI支付对应付退款
     *
     * @param item
     * @param request
     * @param payRecord
     * @return
     */
    private WxPayRefundResponse wxPayPileRefundForPcH5Jsapi(PayChannelItem item, RefundRequest request, PayTradeRecord payRecord) {
        PayValidates.verifyGateway(item.getGateway());
        PayTradeRecord record = saveRefundRecord(request, item);
        record.setTradeNo(payRecord.getTradeNo());
        PayGatewayConfig gatewayConfig = gatewayConfigRepository.queryConfigByNameAndStoreId(PayGatewayEnum.WECHAT,
                request.getStoreId());
        WxPayRefundRequest refundRequest = new WxPayRefundRequest();
        refundRequest.setAppid(gatewayConfig.getAppId());
        refundRequest.setMch_id(gatewayConfig.getAccount());
        refundRequest.setNonce_str(WXPayUtil.generateNonceStr());
        refundRequest.setNotify_url(gatewayConfig.getBossBackUrl() + "/tradeCallback/WXPayPileRefundSuccessCallBack/"+request.getStoreId());
        refundRequest.setOut_refund_no(request.getRefundBusinessId());
        refundRequest.setOut_trade_no(request.getBusinessId());
        refundRequest.setTotal_fee(request.getTotalPrice().multiply(new BigDecimal(100)).
                setScale(0, BigDecimal.ROUND_DOWN).toString());
        refundRequest.setRefund_fee(request.getAmount().multiply(new BigDecimal(100)).
                setScale(0, BigDecimal.ROUND_DOWN).toString());
        try {
            Map<String, String> refundMap = WXPayUtil.objectToMap(refundRequest);
            //获取签名
            String sign = WXPayUtil.generateSignature(refundMap, gatewayConfig.getApiKey());
            refundRequest.setSign(sign);
        } catch (Exception e) {
            e.printStackTrace();
        }
        WxPayRefundResponse wxPayRefundResponse = wxPayService.wxPayRefund(refundRequest, WXPAYATYPE, request.getStoreId());
        if (wxPayRefundResponse.getReturn_code().equals(WXPayConstants.SUCCESS) &&
                wxPayRefundResponse.getResult_code().equals(WXPayConstants.SUCCESS)) {
            record.setTradeNo(wxPayRefundResponse.getTransaction_id());
            recordRepository.save(record);
        } else {
            //退款失败
            log.info(">>>>>>>>>>>>>>>>>>微信退款失败:return_code" + wxPayRefundResponse.getReturn_code() + "respMsg:" + wxPayRefundResponse.getReturn_msg());
            log.info(">>>>>>>>>>>>>>>>>>微信退款失败:err_code" + wxPayRefundResponse.getErr_code() + "respMsg:" + wxPayRefundResponse.getErr_code_des());
            String errMsg = "退款失败原因：";
            if (!wxPayRefundResponse.getReturn_code().equals(WXPayConstants.SUCCESS)) {
                errMsg = errMsg + wxPayRefundResponse.getReturn_msg() + ";";
            }
            if (!wxPayRefundResponse.getResult_code().equals(WXPayConstants.SUCCESS)) {
                errMsg = errMsg + wxPayRefundResponse.getErr_code_des() + ";";
            }
            throw new SbcRuntimeException("K-100212", new Object[]{errMsg});
        }
        return wxPayRefundResponse;
    }

    /**
     * 微信支付--PC、H5、JSAPI支付对应付退款
     *
     * @param item
     * @param request
     * @param payRecord
     * @return
     */
    private WxPayRefundResponse wxPayRefundForPcH5Jsapi(PayChannelItem item, RefundRequest request, PayTradeRecord payRecord) {
        PayValidates.verifyGateway(item.getGateway());
        PayTradeRecord record = saveRefundRecord(request, item);
        record.setTradeNo(payRecord.getTradeNo());
        PayGatewayConfig gatewayConfig = gatewayConfigRepository.queryConfigByNameAndStoreId(PayGatewayEnum.WECHAT,
                request.getStoreId());
        WxPayRefundRequest refundRequest = new WxPayRefundRequest();
        refundRequest.setAppid(gatewayConfig.getAppId());
        refundRequest.setMch_id(gatewayConfig.getAccount());
        refundRequest.setNonce_str(WXPayUtil.generateNonceStr());
        refundRequest.setNotify_url(gatewayConfig.getBossBackUrl() + "/tradeCallback/WXPayRefundSuccessCallBack/"+request.getStoreId());
        refundRequest.setOut_refund_no(request.getRefundBusinessId());
        refundRequest.setOut_trade_no(payRecord.getPayOrderNo());
        refundRequest.setTotal_fee(request.getTotalPrice().multiply(new BigDecimal(100)).
                setScale(0, BigDecimal.ROUND_DOWN).toString());
        refundRequest.setRefund_fee(request.getAmount().multiply(new BigDecimal(100)).
                setScale(0, BigDecimal.ROUND_DOWN).toString());
        try {
            Map<String, String> refundMap = WXPayUtil.objectToMap(refundRequest);
            //获取签名
            String sign = WXPayUtil.generateSignature(refundMap, gatewayConfig.getApiKey());
            refundRequest.setSign(sign);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("refundRequest ==== {}", refundRequest);
        WxPayRefundResponse wxPayRefundResponse = wxPayService.wxPayRefund(refundRequest, WXPAYATYPE, request.getStoreId());
        if (wxPayRefundResponse.getReturn_code().equals(WXPayConstants.SUCCESS) &&
                wxPayRefundResponse.getResult_code().equals(WXPayConstants.SUCCESS)) {
            record.setTradeNo(wxPayRefundResponse.getTransaction_id());
            recordRepository.save(record);
        } else {
            //退款失败
            log.info(">>>>>>>>>>>>>>>>>>微信退款失败:return_code" + wxPayRefundResponse.getReturn_code() + "respMsg:" + wxPayRefundResponse.getReturn_msg());
            log.info(">>>>>>>>>>>>>>>>>>微信退款失败:err_code" + wxPayRefundResponse.getErr_code() + "respMsg:" + wxPayRefundResponse.getErr_code_des());
            String errMsg = "退款失败原因：";
            if (!wxPayRefundResponse.getReturn_code().equals(WXPayConstants.SUCCESS)) {
                errMsg = errMsg + wxPayRefundResponse.getReturn_msg() + ";";
            }
            if (!wxPayRefundResponse.getResult_code().equals(WXPayConstants.SUCCESS)) {
                errMsg = errMsg + wxPayRefundResponse.getErr_code_des() + ";";
            }
            throw new SbcRuntimeException("K-100212", new Object[]{errMsg});
        }
        return wxPayRefundResponse;
    }

    /**
     * 微信囤货支付退款--app支付退款
     *
     * @param item
     * @param request
     * @param payRecord
     * @return
     */
    private WxPayRefundResponse wxPayPileRefundForApp(PayChannelItem item, RefundRequest request, PayTradeRecord payRecord) {
        //微信支付退款
        PayValidates.verifyGateway(item.getGateway());
        PayTradeRecord record = saveRefundRecord(request, item);
        record.setTradeNo(payRecord.getTradeNo());
        PayGatewayConfig gatewayConfig = gatewayConfigRepository.queryConfigByNameAndStoreId(PayGatewayEnum.WECHAT, request.getStoreId());
        WxPayRefundRequest refundRequest = new WxPayRefundRequest();
        refundRequest.setAppid(gatewayConfig.getOpenPlatformAppId());
        refundRequest.setMch_id(gatewayConfig.getOpenPlatformAccount());
        refundRequest.setNonce_str(WXPayUtil.generateNonceStr());
        refundRequest.setOut_refund_no(request.getRefundBusinessId());
        refundRequest.setOut_trade_no(request.getBusinessId());
        refundRequest.setTotal_fee(request.getTotalPrice().multiply(new BigDecimal(100)).
                setScale(0, BigDecimal.ROUND_DOWN).toString());
        refundRequest.setRefund_fee(request.getAmount().multiply(new BigDecimal(100)).
                setScale(0, BigDecimal.ROUND_DOWN).toString());
        refundRequest.setNotify_url(gatewayConfig.getBossBackUrl() + "/tradeCallback/WXPayPileRefundSuccessCallBack/"+request.getStoreId());
        try {
            Map<String, String> refundMap = WXPayUtil.objectToMap(refundRequest);
            //获取签名
            String sign = WXPayUtil.generateSignature(refundMap, gatewayConfig.getOpenPlatformApiKey());
            refundRequest.setSign(sign);
        } catch (Exception e) {
            e.printStackTrace();
        }
        WxPayRefundResponse wxPayRefundResponse = wxPayService.wxPayRefund(refundRequest, WXPAYAPPTYPE, request.getStoreId());
        if (Objects.nonNull(wxPayRefundResponse) && wxPayRefundResponse.getResult_code().equals(WXPayConstants.SUCCESS)) {
            record.setTradeNo(wxPayRefundResponse.getTransaction_id());
            recordRepository.save(record);
        } else {
            //提交退款申请失败
            //退款失败
            log.info(">>>>>>>>>>>>>>>>>>微信退款失败:return_code" + wxPayRefundResponse.getReturn_code() + "respMsg:" + wxPayRefundResponse.getReturn_msg());
            log.info(">>>>>>>>>>>>>>>>>>微信退款失败:err_code" + wxPayRefundResponse.getErr_code() + "respMsg:" + wxPayRefundResponse.getErr_code_des());
            String errMsg = "微信支付退款：";
            if (StringUtils.isNotBlank(wxPayRefundResponse.getErr_code())) {
                errMsg = errMsg + wxPayRefundResponse.getErr_code() + ";";
            }
            if (StringUtils.isNotBlank(wxPayRefundResponse.getErr_code_des())) {
                errMsg = errMsg + wxPayRefundResponse.getErr_code_des() + ";";
            }
            throw new SbcRuntimeException("K-100212", new Object[]{errMsg});
        }
        return wxPayRefundResponse;
    }

    /**
     * 微信支付退款--app支付退款
     *
     * @param item
     * @param request
     * @param payRecord
     * @return
     */
    private WxPayRefundResponse wxPayRefundForApp(PayChannelItem item, RefundRequest request, PayTradeRecord payRecord) {
        //微信支付退款
        PayValidates.verifyGateway(item.getGateway());
        PayTradeRecord record = saveRefundRecord(request, item);
        record.setTradeNo(payRecord.getTradeNo());
        PayGatewayConfig gatewayConfig = gatewayConfigRepository.queryConfigByNameAndStoreId(PayGatewayEnum.WECHAT, request.getStoreId());
        WxPayRefundRequest refundRequest = new WxPayRefundRequest();
        refundRequest.setAppid(gatewayConfig.getOpenPlatformAppId());
        refundRequest.setMch_id(gatewayConfig.getOpenPlatformAccount());
        refundRequest.setNonce_str(WXPayUtil.generateNonceStr());
        refundRequest.setOut_refund_no(request.getRefundBusinessId());
        refundRequest.setOut_trade_no(request.getBusinessId());
        refundRequest.setTotal_fee(request.getTotalPrice().multiply(new BigDecimal(100)).
                setScale(0, BigDecimal.ROUND_DOWN).toString());
        refundRequest.setRefund_fee(request.getAmount().multiply(new BigDecimal(100)).
                setScale(0, BigDecimal.ROUND_DOWN).toString());
        refundRequest.setNotify_url(gatewayConfig.getBossBackUrl() + "/tradeCallback/WXPayRefundSuccessCallBack/"+request.getStoreId());
        try {
            Map<String, String> refundMap = WXPayUtil.objectToMap(refundRequest);
            //获取签名
            String sign = WXPayUtil.generateSignature(refundMap, gatewayConfig.getOpenPlatformApiKey());
            refundRequest.setSign(sign);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("refundRequest ==== {}", refundRequest);
        WxPayRefundResponse wxPayRefundResponse = wxPayService.wxPayRefund(refundRequest, WXPAYAPPTYPE, request.getStoreId());
        if (Objects.nonNull(wxPayRefundResponse) && wxPayRefundResponse.getResult_code().equals(WXPayConstants.SUCCESS)) {
            record.setTradeNo(wxPayRefundResponse.getTransaction_id());
            recordRepository.save(record);
        } else {
            //提交退款申请失败
            //退款失败
            log.info(">>>>>>>>>>>>>>>>>>微信退款失败:return_code" + wxPayRefundResponse.getReturn_code() + "respMsg:" + wxPayRefundResponse.getReturn_msg());
            log.info(">>>>>>>>>>>>>>>>>>微信退款失败:err_code" + wxPayRefundResponse.getErr_code() + "respMsg:" + wxPayRefundResponse.getErr_code_des());
            String errMsg = "微信支付退款：";
            if (StringUtils.isNotBlank(wxPayRefundResponse.getErr_code())) {
                errMsg = errMsg + wxPayRefundResponse.getErr_code() + ";";
            }
            if (StringUtils.isNotBlank(wxPayRefundResponse.getErr_code_des())) {
                errMsg = errMsg + wxPayRefundResponse.getErr_code_des() + ";";
            }
            throw new SbcRuntimeException("K-100212", new Object[]{errMsg});
        }
        return wxPayRefundResponse;
    }

    /**
     * 招商支付退款--app支付退款
     *
     * @param item
     * @param request
     * @param payRecord
     * @return
     */
    private CmbRefundResponse cmbPayRefundForApp(PayChannelItem item, RefundRequest request, PayTradeRecord payRecord) {

        log.info("refundRequest接收参数 ==== {}", JSONObject.toJSONString(request));
        //招商支付退款
        PayValidates.verifyGateway(item.getGateway());
        PayTradeRecord record = saveRefundRecord(request, item);
        record.setTradeNo(payRecord.getTradeNo());
//        PayGatewayConfig gatewayConfig = gatewayConfigRepository.queryConfigByNameAndStoreId(PayGatewayEnum.WECHAT, request.getStoreId());
        CmbPayRefundDataRequest refundRequest = CmbPayRefundDataRequest.builder().build();
        refundRequest.setDate(DateUtil.format(payRecord.getCreateTime(), DateUtil.FMT_TIME_5));
        refundRequest.setOrderNo(request.getBusinessId());
        refundRequest.setRefundSerialNo(request.getRefundBusinessId());
        refundRequest.setAmount(request.getAmount().toString());
        refundRequest.setDesc(request.getDescription());
        log.info("refundRequest ==== {}", JSONObject.toJSONString(refundRequest));
        CmbPayRefundResponse response = cmbService.cmbPayRefund(refundRequest);
        CmbRefundResponse cmbRefundResponse = new CmbRefundResponse();
        cmbRefundResponse.setCmbPayRefundResponse(response);
        cmbRefundResponse.setPayType("CMB");
        if (Objects.nonNull(response) && Objects.nonNull(response.getRspData()) && response.getRspData().getRspCode().equals("SUC0000")) {
            record.setTradeNo(response.getRspData().getRefundSerialNo());
            recordRepository.save(record);
        } else {
            //提交退款申请失败
            //退款失败
            log.info(">>>>>>>>>>>>>>>>>>招商退款失败:return_code" + response.getRspData().getRspCode() + "respMsg:" + response.getRspData().getRspMsg());
            String errMsg = "招商支付退款：";
            if (StringUtils.isNotBlank(response.getRspData().getRspCode())) {
                errMsg = errMsg + response.getRspData().getRspCode() + ";";
            }
            if (StringUtils.isNotBlank(response.getRspData().getRspMsg())) {
                errMsg = errMsg + response.getRspData().getRspMsg() + ";";
            }
//            throw new SbcRuntimeException(errMsg);
            throw new SbcRuntimeException("K-100214", new Object[]{errMsg});
        }
        return cmbRefundResponse;
    }

    /**
     * 银联支付退款--app支付退款
     *
     * @param item
     * @param request
     * @param payRecord
     * @return
     */
    private CupsRefundResponse cupsPayRefundForApp(PayChannelItem item, RefundRequest request, PayTradeRecord payRecord) {

        log.info("refundRequest接收参数 ==== {}", JSONObject.toJSONString(request));
        //银联支付退款
        PayValidates.verifyGateway(item.getGateway());
        PayTradeRecord record = saveRefundRecord(request, item);
        record.setTradeNo(payRecord.getTradeNo());
        PayGatewayEnum payGatewayEnum = payRecord.getChannelItemId().longValue() == 29L ? PayGatewayEnum.CUPSALI : PayGatewayEnum.CUPSWECHAT;
        PayGatewayConfig payGatewayConfig = payDataService.queryConfigByNameAndStoreId(payGatewayEnum,request.getStoreId());

        CupsPayRefundDataRequest refundRequest = CupsPayRefundDataRequest.builder().build();
        refundRequest.setOrderNo(request.getBusinessId());
        refundRequest.setRefundOrderId(request.getRefundBusinessId());
        refundRequest.setAmount(request.getAmount().toString());
        refundRequest.setDesc(request.getDescription());
        refundRequest.setPayOrderNo(payRecord.getPayOrderNo());
        refundRequest.setAppId(payGatewayConfig.getAppId());
        refundRequest.setApiKey(payGatewayConfig.getApiKey());
        refundRequest.setChannelId(payRecord.getChannelItemId());

        log.info("refundRequest ==== {}", JSONObject.toJSONString(refundRequest));
        CupsPayRefundResponse response = cupsService.cupsPayRefund(refundRequest);
        CupsRefundResponse cupsRefundResponse = new CupsRefundResponse();
        cupsRefundResponse.setCupsPayRefundResponse(response);
        cupsRefundResponse.setPayType(record.getChannelItemId().longValue() == 29L ? "CUPSALI" : "CUPSWECHAT");
        if (Objects.nonNull(response) &&  response.getErrCode().equals("SUCCESS")) {
            record.setTradeNo(response.getRefundOrderId());
            recordRepository.save(record);
        } else {
            //提交退款申请失败
            //退款失败
            log.info(">>>>>>>>>>>>>>>>>>银联退款失败:return_code" + response.getErrCode() + "respMsg:" + response.getErrMsg());
            String errMsg = "银联支付退款：";
            if (StringUtils.isNotBlank(response.getErrCode())) {
                errMsg = errMsg + response.getErrCode() + ";";
            }
            if (StringUtils.isNotBlank(response.getErrMsg())) {
                errMsg = errMsg + response.getErrMsg() + ";";
            }
//            throw new SbcRuntimeException(errMsg);
            throw new SbcRuntimeException("K-100214", new Object[]{errMsg});
        }
        return cupsRefundResponse;
    }

    /**
     * app 建行退款
     * @param item
     * @param request
     * @param payRecord
     */
    private Boolean ccbPayRefundForApp(PayChannelItem item, RefundRequest request, PayTradeRecord payRecord) {

        log.info("建行支付退款：接收参数 ==== {}", JSONObject.toJSONString(request));
        //银联支付退款
        PayValidates.verifyGateway(item.getGateway());
        PayTradeRecord record = saveRefundRecord(request, item);

        CcbRefundRequest req = CcbRefundRequest.builder()
                .payTrnNo(payRecord.getTradeNo())
                .custRfndTrcno(request.getRefundBusinessId())
                .rfndAmt(request.getAmount().setScale(2, RoundingMode.DOWN))
                .tid(request.getTid())
                .refundFreight(request.getRefundFreight())
                .freightPrice(request.getFreightPrice())
                .rid(request.getRefundBusinessId())
                .build();


        String result;
        Boolean existRetry = ccbRefundRetryService.existRetryByRid(request.getRefundBusinessId());
        if (existRetry) {
            result = ccbService.retryRefund(request.getRefundBusinessId());
            log.info("建行退款重试返回：{}", result);
        }else {
            // 230816 建行要求 分账后的订单暂不支持退款
            // 230926 去除限制
            // CcbPayRecord ccbPayRecord = ccbPayRecordRepository.findByPyTrnNo(payRecord.getTradeNo());
            // String clrgDt = ccbPayRecord.getClrgDt();
            // String todayStr = DateUtil.format(LocalDateTime.now(), DateUtil.FMT_TIME_5);
            // log.info("建行退款，退单号：{}，日期：{}，应分账日期：{}", request.getRefundBusinessId(), todayStr, clrgDt);
            // if (Integer.parseInt(todayStr) > Integer.parseInt(clrgDt)) {
            //     throw new SbcRuntimeException("K-100215", new Object[]{"建行已分账，暂不支持线上退款，请线下处理。"});
            // }
            result = ccbService.refundOrder(req);
        }

        if (Objects.nonNull(result)) {
            JSONObject obj = JSONObject.parseObject(result);
            String refund_Rsp_St = obj.getString("Refund_Rsp_St");
            if (Objects.equals("00", refund_Rsp_St)) {
                String rfnd_trcno = obj.getString("Rfnd_Trcno");
                if (Objects.nonNull(rfnd_trcno)) {
                    record.setTradeNo(rfnd_trcno);
                    recordRepository.save(record);
                }
                return true;
            }else {
                String errMsg = obj.getString("Refund_Rsp_Inf");
                if (Objects.nonNull(errMsg)) {
                    throw new SbcRuntimeException("K-100215", new Object[]{"建行：" + errMsg});
                }else {
                    throw new SbcRuntimeException("K-100215", new Object[]{"退款失败"});
                }
            }
        }else {
            throw new SbcRuntimeException("K-100215", new Object[]{"退款失败"});
        }
    }

    /**
     * 根据退单与相关订单号号查询退单退款状态
     *
     * @param rid 业务退单号
     * @param tid 业务订单号
     * @return null-无退款记录 | TradeStatus-退款状态
     */
    @Transactional
    public TradeStatus queryRefundResult(String rid, String tid) {
        PayTradeRecord refundRecord = recordRepository.findByBusinessId(rid);
        if (Objects.isNull(refundRecord)) {
            return null;
        }

        if (refundRecord.getStatus() == TradeStatus.SUCCEED) {
            return TradeStatus.SUCCEED;
        }

        if (refundRecord.getStatus() == TradeStatus.PROCESSING && !Objects.isNull(refundRecord.getChargeId())) {
            //处理中退单，跟踪状态
            PayTradeRecord payRecord = recordRepository.findTopByBusinessIdAndStatus(tid, TradeStatus.SUCCEED);
            RefundRecordResult result = queryRefundResult(refundRecord, payRecord.getChargeId());
            if (result.getRecord().getStatus() == TradeStatus.SUCCEED) {
                //更新记录
                recordRepository.updateTradeStatusAndPracticalPriceAndFinishTime(
                        result.getRecord().getId(),
                        result.getRecord().getStatus(),
                        result.getRecord().getPracticalPrice(),
                        result.getRecord().getFinishTime());
                return TradeStatus.SUCCEED;
            } else if (result.getRecord().getStatus() == TradeStatus.PROCESSING) {
                return TradeStatus.PROCESSING;
            } else {
                return TradeStatus.FAILURE;
            }
        }
        return null;
    }


    /**
     * 交易回调,支付模块不执行业务触发，只做更新交易记录操作
     *
     * @param request
     */
    public void callback(@Valid TradeCallbackRequest request) {
        System.err.println("----------------------------1----------------------------------------");
        System.err.println(request.getTradeStatus());
        System.err.println("----------------------------2----------------------------------------");
        PayTradeRecord record = recordRepository.findByChargeId(request.getObjectId());
        if (record.getTradeType() != request.getTradeType()) {
            throw new SbcRuntimeException("K-100204");
        }
        record.setStatus(request.getTradeStatus());
        record.setCallbackTime(record.getCallbackTime() == null ? LocalDateTime.now() : record.getCallbackTime());
        record.setPracticalPrice(request.getAmount());
        record.setFinishTime(request.getFinishTime());
        recordRepository.saveAndFlush(record);
    }

    /**
     * 根据授权码获取微信授权用户openId
     *
     * @param code
     */
    public String getWxOpenIdAndStoreId(String code,Long storeId) {
        List<PayGatewayConfig> configs = gatewayConfigRepository.queryConfigByOpenAndStoreId(storeId);
        Optional<PayGatewayConfig> optional = configs.stream().filter(
                c ->
                        c.getPayGateway().getName() == PayGatewayEnum.WECHAT
                                ||
                                (
                                        c.getPayGateway().getType()
                                                &&
                                                c.getPayGateway().getPayChannelItemList().stream().anyMatch(
                                                        i -> "WeChat".equals(i.getChannel())
                                                                &&
                                                                i.getIsOpen() == IsOpen.YES
                                                )
                                )

        ).findFirst();
        return optional.map(config -> proxy.getWxOpenId(config, code)).orElse(null);
    }

    /**
     * 银联企业支付
     *
     * @param unionPay
     * @return
     */
    public String unionB2BPay(UnionPayRequest unionPay) {
        //是否重复支付
        PayTradeRecord record = recordRepository.findByBusinessId(unionPay.getBusinessId());
        String html = "";
        if (!Objects.isNull(record) && record.getStatus() == TradeStatus.SUCCEED) {
            //如果重复支付，判断状态，已成功状态则做异常提示
            throw new SbcRuntimeException("K-100203");
        } else {
            if (record == null) {
                record = new PayTradeRecord();
                record.setId(GeneratorUtils.generatePT());
            }
            record.setApplyPrice(unionPay.getAmount());
            record.setBusinessId(unionPay.getBusinessId());
            record.setClientIp(unionPay.getClientIp());
            record.setChannelItemId(unionPay.getChannelItemId());
            record.setTradeType(TradeType.PAY);
            record.setCreateTime(LocalDateTime.now());
            record.setStatus(TradeStatus.PROCESSING);
            recordRepository.saveAndFlush(record);
            html = createUnionHtml(unionPay);
        }
        return html;
    }

    /**
     * 银联企业支付同步回调 添加 数据
     *
     * @param resMap
     */
    public void unionCallBack(Map<String, String> resMap, PayTradeRecord record) {
        /*record.setTradeNo(resMap.get("queryId"));
        if ("00".equals(resMap.get("respCode"))) {
            record.setStatus(TradeStatus.SUCCEED);
        } else {
            record.setStatus(TradeStatus.FAILURE);
        }
        record.setCallbackTime(record.getCallbackTime() == null ? LocalDateTime.now() : record.getCallbackTime());
        record.setPracticalPrice(new BigDecimal(Long.parseLong(resMap.get("txnAmt")) / 100));
        record.setFinishTime(LocalDateTime.now());
        recordRepository.saveAndFlush(record);*/
        // TODO: 2020/9/23
        if ("0000".equals(resMap.get("OrderStatus"))) {
            record.setStatus(TradeStatus.SUCCEED);
        } else {
            record.setStatus(TradeStatus.FAILURE);
        }
        record.setCallbackTime(record.getCallbackTime() == null ? LocalDateTime.now() : record.getCallbackTime());
        // TODO: 2020/9/23
        record.setPracticalPrice(new BigDecimal(NumberUtils.toLong(resMap.get("OrderAmt")) / 100));
        record.setFinishTime(LocalDateTime.now());
        recordRepository.saveAndFlush(record);
    }

    /**
     * 微信支付同步回调 添加 数据
     *
     * @param request
     * @param record
     */
    public void wxPayCallBack(PayTradeRecordRequest request, PayTradeRecord record) {
        log.info("招商当前订单交易成功后的状态111111：" + request.getResult_code());
        record.setTradeNo(request.getTradeNo());
        if (request.getResult_code().equals(WXPayConstants.SUCCESS)) {
            record.setStatus(TradeStatus.SUCCEED);
            log.info("招商当前订单交易成功后的状态222223：" + request.getResult_code() + " == ");
        } else {
            record.setStatus(TradeStatus.FAILURE);
            log.info("招商当前订单交易成功后的状态222224：" + request.getResult_code() + " == ");
        }

        //招商返回的支付类型
        if(request.getChannelItemId() != null && request.getChannelItemId() == 28L){
            record.setChargeId(request.getChargeId());
        }

        record.setCallbackTime(record.getCallbackTime() == null ? LocalDateTime.now() : record.getCallbackTime());
        record.setPracticalPrice(request.getPracticalPrice());
        record.setFinishTime(LocalDateTime.now());
        record.setTradeNo(request.getTradeNo());
        record.setDiscountAmount(request.getDiscountAmount());
        log.info("招商当前订单交易成功后的状态222224：" + record.getStatus() + " == ");
        recordRepository.saveAndFlush(record);

    }

    /**
     * 添加交易记录
     *
     * @param recordRequest
     */
    @Transactional
    public void addPayTradeRecord(PayTradeRecordRequest recordRequest) {
        PayTradeRecord record = new PayTradeRecord();
        KsBeanUtil.copyPropertiesThird(recordRequest, record);
        record.setId(GeneratorUtils.generatePT());
        if (recordRequest.getResult_code().equals(WXPayConstants.SUCCESS)) {
            record.setStatus(TradeStatus.SUCCEED);
        } else {
            record.setStatus(TradeStatus.FAILURE);
        }
        record.setTradeType(TradeType.PAY);
        record.setFinishTime(LocalDateTime.now());
        record.setCreateTime(LocalDateTime.now());
        recordRepository.deleteByBusinessId(recordRequest.getBusinessId());
        recordRepository.saveAndFlush(record);
    }

    /**
     * 新银联企业支付
     *
     * @param unionPay
     * @return
     */
    public String newUnionB2BPay(UnionPayRequest unionPay) {
        //是否重复支付
        PayTradeRecord record = recordRepository.findByBusinessId(unionPay.getBusinessId());
        String html = "";
        if (!Objects.isNull(record) && record.getStatus() == TradeStatus.SUCCEED) {
            //如果重复支付，判断状态，已成功状态则做异常提示
            throw new SbcRuntimeException("K-100203");
        } else {
            if (record == null) {
                record = new PayTradeRecord();
                record.setId(GeneratorUtils.generatePT());
            }
            record.setApplyPrice(unionPay.getAmount());
            record.setBusinessId(unionPay.getBusinessId());
            record.setClientIp(unionPay.getClientIp());
            record.setChannelItemId(unionPay.getChannelItemId());
            record.setTradeType(TradeType.PAY);
            record.setCreateTime(LocalDateTime.now());
            record.setStatus(TradeStatus.PROCESSING);
            recordRepository.saveAndFlush(record);
            html = newCreateUnionHtml(unionPay);
        }
        return html;
    }

    /**
     * 银联企业支付参数拼接
     *
     * @param unionPay
     * @return
     */
    private String createUnionHtml(UnionPayRequest unionPay) {


        SDKConfig.getConfig().loadPropertiesFromSrc();
        //前台页面传过来的
        String merId = unionPay.getApiKey();
        BigDecimal txnAmt = unionPay.getAmount();

        Map<String, String> requestData = new HashMap<>();

        /***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
        requestData.put("version", SDKConfig.getConfig().getVersion());                  //版本号，全渠道默认值
        requestData.put("encoding", "UTF-8");          //字符集编码，可以使用UTF-8,GBK两种方式
        requestData.put("signMethod", SDKConfig.getConfig().getSignMethod()); //签名方法
        requestData.put("txnType", "01");                          //交易类型 ，01：消费
        requestData.put("txnSubType", "01");                          //交易子类型， 01：自助消费
        requestData.put("bizType", "000202");                      //业务类型 000202: B2B
        requestData.put("channelType", "07");                      //渠道类型 固定07

        /***商户接入参数***/
        requestData.put("merId", merId);                              //商户号码，请改成自己申请的正式商户号或者open上注册得来的777测试商户号
        requestData.put("accessType", "0");                          //接入类型，0：直连商户
        requestData.put("orderId", unionPay.getBusinessId());             //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则
        requestData.put("txnTime", getCurrentTime());
        //订单发送时间，取系统时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
        requestData.put("currencyCode", "156");                      //交易币种（境内商户一般是156 人民币）
        requestData.put("txnAmt", txnAmt.multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP)
                .toString());                              //交易金额，单位分，不要带小数点

        //前台通知地址 （需设置为外网能访问 http https均可），支付成功后的页面 点击“返回商户”按钮的时候将异步通知报文post到该地址
        //如果想要实现过几秒中自动跳转回商户页面权限，需联系银联业务申请开通自动返回商户权限
        //异步通知参数详见open.unionpay.com帮助中心 下载  产品接口规范  网关支付产品接口规范 消费交易 商户通知
        requestData.put("frontUrl", unionPay.getFrontUrl());

        //后台通知地址（需设置为【外网】能访问 http https均可），支付成功后银联会自动将异步通知报文post到商户上送的该地址，失败的交易银联不会发送后台通知
        //后台通知参数详见open.unionpay.com帮助中心 下载  产品接口规范  网关支付产品接口规范 消费交易 商户通知
        //注意:1.需设置为外网能访问，否则收不到通知    2.http https均可  3.收单后台通知后需要10秒内返回http200或302状态码
        //    4.如果银联通知服务器发送通知后10秒内未收到返回状态码或者应答码非http200，那么银联会间隔一段时间再次发送。总共发送5次，每次的间隔时间为0,1,2,4分钟。
        //    5.后台通知地址如果上送了带有？的参数，例如：http://abc/web?a=b&c=d 在后台通知处理程序验证签名之前需要编写逻辑将这些字段去掉再验签，否则将会验签失败
        requestData.put("backUrl", unionPay.getNotifyUrl());

        //实现网银前置的方法：
        //上送issInsCode字段，该字段的值参考《平台接入接口规范-第5部分-附录》（全渠道平台银行名称-简码对照表）2）联系银联业务运营部门开通商户号的网银前置权限
        //requestData.put("issInsCode", "ABC");                 //发卡机构代码

        // 订单超时时间。
        // 超过此时间后，除网银交易外，其他交易银联系统会拒绝受理，提示超时。 跳转银行网银交易如果超时后交易成功，会自动退款，大约5个工作日金额返还到持卡人账户。
        // 此时间建议取支付时的北京时间加15分钟。
        // 超过超时时间调查询接口应答origRespCode不是A6或者00的就可以判断为失败。
        requestData.put("payTimeout", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date().getTime() + 15 * 60 *
                1000));

        // 请求方保留域，
        // 透传字段，查询、通知、对账文件中均会原样出现，如有需要请启用并修改自己希望透传的数据。
        // 出现部分特殊字符时可能影响解析，请按下面建议的方式填写：
        // 1. 如果能确定内容不会出现&={}[]"'等符号时，可以直接填写数据，建议的方法如下。
//		requestData.put("reqReserved", "透传信息1|透传信息2|透传信息3");
        // 2. 内容可能出现&={}[]"'符号时：
        // 1) 如果需要对账文件里能显示，可将字符替换成全角＆＝｛｝【】“‘字符（自己写代码，此处不演示）；
        // 2) 如果对账文件没有显示要求，可做一下base64（如下）。
        //    注意控制数据长度，实际传输的数据长度不能超过1024位。
        //    查询、通知等接口解析时使用new String(Base64.decodeBase64(reqReserved), DemoBase.encoding);解base64后再对数据做后续解析。
//		requestData.put("reqReserved", Base64.encodeBase64String("任意格式的信息都可以".toString().getBytes(DemoBase.encoding)));

        /**请求参数设置完毕，以下对请求参数进行签名并生成html表单，将表单写入浏览器跳转打开银联页面**/
        Map<String, String> reqData = AcpService.sign(requestData, "UTF-8");  //报文中certId,
        // signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
        String requestFrontUrl = SDKConfig.getConfig().getFrontRequestUrl();  //获取请求银联的前台地址：对应属性文件acp_sdk
        // .properties文件中的acpsdk.frontTransUrl
        String html = AcpService.createAutoFormHtml(requestFrontUrl, reqData, "UTF-8");   //生成自动跳转的Html表单

        log.info("打印请求HTML，此为请求报文，为联调排查问题的依据：" + html);
        //将生成的html写到浏览器中完成自动跳转打开银联支付页面；这里调用signData之后，将html写到浏览器跳转到银联页面之前均不能对html中的表单项的名称和值进行修改，如果修改会导致验签不通过
        return html;
    }

    private Map<String, String> unionRefund(PayTradeRecord record, PayGatewayConfig gatewayConfig) {

        SDKConfig.getConfig().loadPropertiesFromSrc();
        String origQryId = record.getTradeNo();
        String txnAmt = record.getApplyPrice().multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP)
                .toString();
        String encoding = "UTF-8";
        Map<String, String> data = new HashMap<String, String>();

        /***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
        data.put("version", SDKConfig.getConfig().getVersion());                  //版本号，全渠道默认值
        data.put("encoding", encoding);             //字符集编码 可以使用UTF-8,GBK两种方式
        data.put("signMethod", SDKConfig.getConfig().getSignMethod()); //签名方法
        data.put("txnType", "04");                           //交易类型 04-退货
        data.put("txnSubType", "00");                        //交易子类型  默认00
        data.put("bizType", "000202");                       //业务类型
        data.put("channelType", "07");                       //渠道类型，07-PC，08-手机

        /***商户接入参数***/
        data.put("merId", gatewayConfig.getApiKey());                //商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
        data.put("accessType", "0");                         //接入类型，商户接入固定填0，不需修改
        data.put("orderId", record.getBusinessId());          //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则，重新产生，不同于原消费
        data.put("txnTime", getCurrentTime());      //订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
        data.put("currencyCode", "156");                     //交易币种（境内商户一般是156 人民币）
        data.put("txnAmt", txnAmt);                          //****退货金额，单位分，不要带小数点。退货金额小于等于原消费金额，当小于的时候可以多次退货至退货累计金额等于原消费金额
        data.put("backUrl", gatewayConfig.getBossBackUrl() + "/tradeCallback/unionRefundCallBack");
        //后台通知地址，后台通知参数详见open.unionpay
        // .com帮助中心 下载  产品接口规范
        // 网关支付产品接口规范 退货交易 商户通知,
        // 其他说明同消费交易的后台通知

        /***要调通交易以下字段必须修改***/
        data.put("origQryId", origQryId);      //****原消费交易返回的的queryId，可以从消费交易后台通知接口中或者交易状态查询接口中获取

        // 请求方保留域，
        // 透传字段，查询、通知、对账文件中均会原样出现，如有需要请启用并修改自己希望透传的数据。
        // 出现部分特殊字符时可能影响解析，请按下面建议的方式填写：
        // 1. 如果能确定内容不会出现&={}[]"'等符号时，可以直接填写数据，建议的方法如下。
//		data.put("reqReserved", "透传信息1|透传信息2|透传信息3");
        // 2. 内容可能出现&={}[]"'符号时：
        // 1) 如果需要对账文件里能显示，可将字符替换成全角＆＝｛｝【】“‘字符（自己写代码，此处不演示）；
        // 2) 如果对账文件没有显示要求，可做一下base64（如下）。
        //    注意控制数据长度，实际传输的数据长度不能超过1024位。
        //    查询、通知等接口解析时使用new String(Base64.decodeBase64(reqReserved), DemoBase.encoding);解base64后再对数据做后续解析。
//		data.put("reqReserved", Base64.encodeBase64String("任意格式的信息都可以".toString().getBytes(DemoBase.encoding)));

        /**请求参数设置完毕，以下对请求参数进行签名并发送http post请求，接收同步应答报文------------->**/

        Map<String, String> reqData = AcpService.sign(data, encoding);//报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
        String url = SDKConfig.getConfig().getBackRequestUrl();                                //交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.backTransUrl
        Map<String, String> rspData = AcpService.post(reqData, url, encoding);//这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
        /**对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考------------->**/
        //应答码规范参考open.unionpay.com帮助中心 下载  产品接口规范  《平台接入接口规范-第5部分-附录》
//        if(!rspData.isEmpty()){
//            return  rspData.get("respCode");
//        }
//        return null;
        return rspData;

    }

    private PayRecordResult queryPayResult(PayTradeRecord record) {
        PayChannelItem item = channelItemRepository.findById(record.getChannelItemId()).get();

        return proxy.queryPayResult(record, item.getGateway());
    }

    private RefundRecordResult queryRefundResult(PayTradeRecord record, String payObjectId) {
        PayChannelItem item = channelItemRepository.findById(record.getChannelItemId()).get();
        PayGateway gateway = item.getGateway();
        return proxy.queryRefundResult(record, payObjectId, gateway);
    }


    private PayChannelItem getPayChannelItem(Long channelItemId,Long storeId) {
        PayChannelItem item = channelItemRepository.findById(channelItemId).get();
        PayValidates.verfiyPayChannelItem(item);
        // 获取网关
        PayGateway gateway = gatewayRepository.queryByNameAndStoreId(item.getGatewayName(),storeId);
        item.setGateway(gateway);
        return item;
    }

    private void savePayRecord(PayRequest request, PayResult result) {
        PayTradeRecord record = new PayTradeRecord();
        record.setId(GeneratorUtils.generatePT());
        record.setApplyPrice(request.getAmount());
        record.setBusinessId(request.getBusinessId());
        record.setChargeId(result.getObjectId());
        record.setClientIp(request.getClientIp());
        record.setChannelItemId(request.getChannelItemId());
        record.setTradeNo(result.getTradeNo());
        record.setTradeType(TradeType.PAY);
        record.setCreateTime(result.getCreateTime());
        record.setStatus(TradeStatus.PROCESSING);
        recordRepository.deleteByBusinessId(request.getBusinessId());
        recordRepository.saveAndFlush(record);
    }

    private PayTradeRecord saveRefundRecord(RefundRequest request, PayChannelItem channelItem) {
        PayTradeRecord record = new PayTradeRecord();
        record.setId(GeneratorUtils.generatePT());
        record.setApplyPrice(request.getAmount());
        record.setBusinessId(request.getRefundBusinessId());
        record.setClientIp(request.getClientIp());
        record.setChannelItemId(channelItem.getId());
        record.setTradeType(TradeType.REFUND);
        record.setStatus(TradeStatus.PROCESSING);
        record.setCreateTime(LocalDateTime.now());
        //删除失败或未成功获取到退款对象的记录
        recordRepository.deleteByBusinessId(request.getRefundBusinessId());
        record = recordRepository.saveAndFlush(record);
        return record;
    }

    // 商户发送交易时间 格式:YYYYMMDDhhmmss
    public static String getCurrentTime() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    // AN8..40 商户订单号，不能含"-"或"_"
    public static String getOrderId() {
        return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    }

    protected SecssUtil getSecssUtil() {
        //   String path = "D:\\workspace\\jew\\back\\sbc-service-pay\\service-pay-api\\src\\main\\resources\\security.properties";
        SecssUtil secssUtil = new SecssUtil();

        secssUtil.init(url);
        // secssUtil.init(path);
        return secssUtil;
    }

    /**
     * 新银联企业支付参数拼接
     *
     * @param unionPay
     * @return
     */
    private String newCreateUnionHtml(UnionPayRequest unionPay) {

        //前台页面传过来的
        /***商户接入参数***/
        String merId = unionPay.getApiKey();
        BigDecimal txnAmt = unionPay.getAmount();

        Map<String, String> requestData = new HashMap<>();

        requestData.put("Version", newVersion);  //版本号，认证支付和快捷支付：20150922其余：20140728

        requestData.put("TranType", "0002");                          //交易类型 ，0002 企业网银支付

        requestData.put("BusiType", "0001");                      //业务类型 000202: B2B

        requestData.put("MerId", merId);                              //商户号码，请改成自己申请的正式商户号或者open上注册得来的777测试商户号
        requestData.put("AccessType", "0");                          //接入类型，0：直连商户
        requestData.put("MerOrderNo", unionPay.getBusinessId());             //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则
        requestData.put("TranDate", getTranDate());           // 商户提交交易的日期
        requestData.put("TranTime", getTranTime());            //商户提交交易的时间 格式:hhmmss
        requestData.put("OrderAmt", txnAmt.multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP)
                .toString());                              //交易金额，单位分，不要带小数点
        requestData.put("MerPageUrl", unionPay.getFrontUrl());

        requestData.put("MerBgUrl", unionPay.getNotifyUrl());

        // 订单超时时间。
        requestData.put("PayTimeOut", "60");
        requestData.put("RemoteAddr",unionPay.getClientIp());   // 防钓鱼客户浏  览器 IP

        /**请求参数设置完毕，以下对请求参数进行签名并生成html表单，将表单写入浏览器跳转打开银联页面**/
        SecssUtil secssUtil = getSecssUtil();
        //签名
        secssUtil.sign(requestData);
        String signature = secssUtil.getSign();
        log.info("=== {}",secssUtil.getSign());
        requestData.put("Signature",signature);
//

        requestData.put("Signature",secssUtil.getSign()) ;

        secssUtil.verify(requestData);
        log.info("=== {}  : {}",secssUtil.getErrCode(),secssUtil.getErrMsg());
        String html = AcpService.createAutoFormHtml(requestFrontUrl, requestData, "UTF-8");   //生成自动跳转的Html表单

        log.info("打印请求HTML，此为请求报文，为联调排查问题的依据：" + html);

        return html;
    }

    // 商户提交交易的日期 格式:YYYYMMDD
    private static String getTranDate() {
        return new SimpleDateFormat("yyyyMMdd").format(new Date());
    }
    // 商户提交交易的时间 格式:hhmmss
    private static String getTranTime() {
        return new SimpleDateFormat("HHmmss").format(new Date());
    }

    /**
     * 新银联退款
     * @param record
     * @param gatewayConfig
     * @param payRecord
     * @return
     */
    private Map<String, String> newUnionRefund(PayTradeRecord record, PayGatewayConfig gatewayConfig, PayTradeRecord payRecord) {

        SDKConfig.getConfig().loadPropertiesFromSrc();
        String origQryId = record.getTradeNo();
        String txnAmt = record.getApplyPrice().multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP)
                .toString();
        String encoding = "UTF-8";
        Map<String, String> data = new HashMap<String, String>();
        /***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
        data.put("Version", newVersion);                  //版本号，
        data.put("TranType", "0401");                           //交易类型 0401退款
        data.put("BusiType", "0001");                       //业务类型
        /***商户接入参数***/
//        {channelItemId=11,
//                finishTime=2020-11-20T15:46:59,
//                tradeNo=null, practicalPrice=652.00, businessId=O202011201524180717, callbackTime=2020-11-20T15:46:59,
//                createTime=2020-11-20T15:45:03, chargeId=null, clientIp=123.179.185.18,
//                id=PT202011201528316740, applyPrice=652.00, tradeType=PAY, status=SUCCEED
//        }
//        openPlatformAppId=, apiKey=594312008310001, openPlatformSecret=, publicKey=, secret=,
//                payGateway=com.wanmi.sbc.pay.model.root.PayGateway#373, storeId=-99, wxPayCertificate=null, openPlatformApiKey=,
//                wxOpenPayCertificate=null, pcWebUrl=https://b.jslink.com, privateKey=, appId2=, bossBackUrl=https://sbff.jslink.com,
//        // createTime=2020-08-17T08:42:48, appId=, openPlatformAccount=, id=222, account=594312008310001, pcBackUrl=https://pbff.jslink.com}
        data.put("MerId", gatewayConfig.getApiKey());                //商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
        data.put("AccessType", "0");                         //接入类型，商户接入固定填0，不需修改
        data.put("MerOrderNo", record.getBusinessId());          //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则，重新产生，不同于原消费
        data.put("TranDate", getTranDate());      //订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
        data.put("TranTime", getTranTime());      //订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
        data.put("OriOrderNo",payRecord.getBusinessId());  //原始交易订单号
        data.put("OriTranDate",payRecord.getCreateTime().format(DateTimeFormatter.ofPattern("yyyyMMdd")));  //原始交易时间
//        data.put("OrderAmt", txnAmt);                          //****退货金额，单位分，不要带小数点。退货金额小于等于原消费金额，当小于的时候可以多次退货至退货累计金额等于原消费金额
        data.put("RefundAmt", txnAmt);                          //****退货金额，单位分，不要带小数点。退货金额小于等于原消费金额，当小于的时候可以多次退货至退货累计金额等于原消费金额
        data.put("MerBgUrl", gatewayConfig.getBossBackUrl() + "/tradeCallback/newUnionRefundCallBack");
        /**请求参数设置完毕，以下对请求参数进行签名并发送http post请求，接收同步应答报文------------->**/
//        SecssUtil secssUtil = new SecssUtil();
        SecssUtil secssUtil1 = getSecssUtil();
        secssUtil1.sign(data);
        String signature = secssUtil1.getSign();
        data.put("Signature",signature);
        log.info("数据源======={}",data);
        String sendMap = AcpService.send(unionRefundUrl, data);
        // 解析同步应答字段
        String[] strs = sendMap.split("&", -1);
        Map<String, String> resultMap = new TreeMap<String, String>();
        for (String str : strs) {
            String[] keyValues = str.split("=", -1);
            if (keyValues.length < 2) {
                continue;
            }
            String key = keyValues[0];
            String value = keyValues[1];
            if (StringUtil.isEmpty(value)) {
                continue;
            }
            resultMap.put(key, value);
        }

        return resultMap;

    }

    // @LcnTransaction
    @Transactional(noRollbackFor = SbcRuntimeException.class)
    public Boolean refundByChannel(RefundByChannelRequest refundByChannelRequest) {
        Map<String, List<RefundByChannelRequest.RefundItem>> refundItemsByOrderMap
                = refundByChannelRequest.getPayTypeRefundItemsByOrderMap();
        if (MapUtils.isEmpty(refundItemsByOrderMap)) {
            log.info("refundByChannel:: refundItemsByOrderMap 集合为空，无需处理");
            return true;
        }

        refundItemsByOrderMap.forEach((orderId, refundItems) -> {
            doRefundByChannel(orderId, refundItems);
        });
        return true;
    }

    private void doRefundByChannel(String orderId, List<RefundByChannelRequest.RefundItem> refundItems) {
        if (CollectionUtils.isEmpty(refundItems) || StringUtils.isBlank(orderId)) {
            log.info("doRefundByChannel：参数为空，不再处理");
            return;
        }

        log.info("按渠道进行退款：开始处理订单号={}", orderId);
        refundItems.forEach(refundItem -> {
            Assert.notNull(refundItem, "refundItem 不能为null");
            Assert.notNull(refundItem.getRefundType(), "refundItem.refundType 不能为null");
            if (RefundType.ONLINE == refundItem.getRefundType()) {
                doRefundOnline(refundItem);
            } else if (RefundType.BALANCE == refundItem.getRefundType()) {
                doRefundBalance(refundItem);
            } else {
                throw new RuntimeException("不支持的退款类型" + refundItem.getRefundType());
            }
        });
        log.info("按渠道进行退款：结束处理订单号={}", orderId);
    }

    private void doRefundBalance(RefundByChannelRequest.RefundItem refundItem) {
        Assert.notEmpty(refundItem.getExtraInfos(), "extraInfos 不能为空");
        Map<String, String> extraInfos = refundItem.getExtraInfos();
        Assert.hasText(extraInfos.get("buyerId"), "extraInfos.buyerId 不能为空");
        Assert.hasText(extraInfos.get("account"), "extraInfos.account 不能为空");
        Assert.hasText(extraInfos.get("activityType"), "extraInfos.activityType 不能为空");
        Assert.hasText(extraInfos.get("storeId"), "extraInfos.storeId 不能为空");

        String relationId = RefundSourceType.CANCEL_ORDER == refundItem.getSourceType() ?
                refundItem.getBizId() :
                refundItem.getRefundBizId();

        WalletDetailsType walletDetailsType = RefundSourceType.CANCEL_ORDER == refundItem.getSourceType() ?
                WalletDetailsType.INCREASE_ORDER_CANCEL :
                WalletDetailsType.INCREASE_ORDER_REFUND;
        /*
        ModifyWalletBalanceForRefundRequest modifyRequest = new ModifyWalletBalanceForRefundRequest();
        modifyRequest.setWalletDetailsType(walletDetailsType);
        modifyRequest.setRelationId(relationId);
        modifyRequest.setAmount(refundItem.getRefundAmount());
        modifyRequest.setTradeRemark(refundItem.getDescription());
        modifyRequest.setRemark(refundItem.getDescription());
        modifyRequest.setBuyerId(extraInfos.get("buyerId"));
        modifyRequest.setCustomerAccount(extraInfos.get("account"));
        modifyRequest.setActivityType(extraInfos.get("activityType"));
        modifyRequest.setStoreId(extraInfos.get("storeId"));
        modifyRequest.setBudgetType(BudgetType.INCOME);
        if(modifyRequest.getRelationId().startsWith(GeneratorService._PREFIX_PROVIDER_CLAIMS)){
            Object chaimApplyType= extraInfos.get("chaimApplyType");
            if(null!=chaimApplyType) {
                if(chaimApplyType.equals("0")) {//鲸币充值手动充值
                    modifyRequest.setWalletDetailsType(WalletDetailsType.GIVE_JINGBI);
                }
                if(chaimApplyType.equals("1")) {//鲸币充值手动抵扣
                    modifyRequest.setWalletDetailsType(WalletDetailsType.BACK_JINGBI);
                    modifyRequest.setBudgetType(BudgetType.EXPENDITURE);
                }
            }
        }

        customerWalletProvider.modifyWalletBalanceForRefundV2(modifyRequest);*/

        //取消订单，返还鲸币余额 @jkp
        WalletRecordTradeType tradeType = WalletRecordTradeType.BALANCE_REFUND;//钱包交易记录交易类型
        int operationType = 0;//操作类型：固定传1

        if(relationId.startsWith(GeneratorService._PREFIX_PROVIDER_CLAIMS)){
            Object chaimApplyType= extraInfos.get("chaimApplyType");
            if(null!=chaimApplyType) {
                if(chaimApplyType.equals("0")) {//鲸币充值手动充值
                    tradeType = WalletRecordTradeType.GIVE;
                }
                if(chaimApplyType.equals("1")) {//鲸币充值手动抵扣
                    tradeType = WalletRecordTradeType.BACK;
                    operationType = 1;
                }
            }
        }

        CustomerWalletGiveRequest giveRequest = CustomerWalletGiveRequest.builder()
                .customerId(extraInfos.get("buyerId"))
                .customerAccount(extraInfos.get("account"))
                .opertionType(operationType)
                .balance(refundItem.getRefundAmount())
                .storeId(refundItem.getStoreId().toString())
                .relationOrderId(relationId)
                .remark(walletDetailsType.getDesc())
                .tradeRemark(walletDetailsType.getDesc()+"-"+relationId)
                .dealTime(LocalDateTime.now())
                .walletRecordTradeType(tradeType)
                .build();
        BaseResponse<WalletRecordVO> response = walletMerchantProvider.merchantGiveUser(giveRequest);
        if (response==null || !response.getCode().equals(ResultCode.SUCCESSFUL)){
            throw new SbcRuntimeException("余额执行操作失败, 请重新操作! ");
        }
    }

    private void doRefundOnline(RefundByChannelRequest.RefundItem refundItem) {
        RefundRequest refundRequest = new RefundRequest();
        refundRequest.setRefundBusinessId(refundItem.getRefundBizId());
        refundRequest.setBusinessId(refundItem.getBizId());
        refundRequest.setAmount(refundItem.getRefundAmount());
        refundRequest.setTotalPrice(refundItem.getTotalOnlineTradePrice());
        refundRequest.setClientIp("127.0.0.1");
        refundRequest.setDescription(refundItem.getDescription());
        refundRequest.setStoreId(refundItem.getStoreId());
        refundRequest.setTid(refundItem.getTid());
        refundRequest.setRefundFreight(refundItem.getRefundFreight());
        refundRequest.setFreightPrice(refundItem.getFreightPrice());
        this.refundNew(refundRequest);
    }
}

