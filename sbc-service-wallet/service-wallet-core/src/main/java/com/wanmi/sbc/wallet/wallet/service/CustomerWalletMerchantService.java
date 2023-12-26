package com.wanmi.sbc.wallet.wallet.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.internal.util.StringUtils;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.AccountType;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.*;
import com.wanmi.sbc.common.util.tencent.TencentImCustomerUtil;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoQueryProvider;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.customer.NoDeleteCustomerGetByAccountRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeByAccountNameRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeListRequest;
import com.wanmi.sbc.customer.api.request.store.StoreByCompanyInfoIdRequest;
import com.wanmi.sbc.customer.api.request.store.StoreByIdRequest;
import com.wanmi.sbc.customer.api.response.customer.NoDeleteCustomerGetByAccountResponse;
import com.wanmi.sbc.customer.api.response.employee.EmployeeByAccountNameResponse;
import com.wanmi.sbc.customer.api.response.employee.EmployeeListResponse;
import com.wanmi.sbc.customer.api.response.store.StoreByIdResponse;
import com.wanmi.sbc.customer.bean.vo.EmployeeListVO;
import com.wanmi.sbc.customer.bean.vo.EmployeeVO;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.goods.bean.enums.SaleType;
import com.wanmi.sbc.order.api.provider.trade.TradeProvider;
import com.wanmi.sbc.order.api.request.trade.CoinActivityPushKingdeeRequest;
import com.wanmi.sbc.pay.api.provider.PayQueryProvider;
import com.wanmi.sbc.pay.api.request.GatewayConfigByGatewayRequest;
import com.wanmi.sbc.pay.api.request.PayTradeRecordRequest;
import com.wanmi.sbc.pay.api.response.PayGatewayConfigResponse;
import com.wanmi.sbc.pay.api.response.PayTradeRecordResponse;
import com.wanmi.sbc.pay.api.response.WxPayResultResponse;
import com.wanmi.sbc.pay.bean.enums.PayGatewayEnum;
import com.wanmi.sbc.pay.weixinpaysdk.WXPayConstants;
import com.wanmi.sbc.pay.weixinpaysdk.WXPayUtil;
import com.wanmi.sbc.setting.api.provider.systemconfig.SystemConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.systemconfig.SystemConfigQueryRequest;
import com.wanmi.sbc.setting.api.response.systemconfig.SystemConfigResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigKey;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.utils.AccountUtils;
import com.wanmi.sbc.wallet.api.request.wallet.*;
import com.wanmi.sbc.wallet.api.response.wallet.WalletCountResponse;
import com.wanmi.sbc.wallet.api.response.wallet.WalletInfoResponse;
import com.wanmi.sbc.wallet.api.response.wallet.WalletListResponse;
import com.wanmi.sbc.wallet.bean.enums.BudgetType;
import com.wanmi.sbc.wallet.bean.enums.TradeStateEnum;
import com.wanmi.sbc.wallet.bean.enums.WalletDetailsType;
import com.wanmi.sbc.wallet.bean.enums.WalletRecordTradeType;
import com.wanmi.sbc.wallet.bean.vo.CustomerWalletStoreIdVO;
import com.wanmi.sbc.wallet.bean.vo.TicketsFormQueryVO;
import com.wanmi.sbc.wallet.bean.vo.WalletRecordVO;
import com.wanmi.sbc.wallet.paycallbackresult.service.PayCallBackResultService;
import com.wanmi.sbc.wallet.wallet.model.root.CustomerWallet;
import com.wanmi.sbc.wallet.wallet.model.root.TicketsForm;
import com.wanmi.sbc.wallet.wallet.model.root.WalletRecord;
import com.wanmi.sbc.wallet.wallet.repository.CustomerWalletQueryRepository;
import com.wanmi.sbc.wallet.wallet.repository.CustomerWalletRepository;
import com.wanmi.sbc.wallet.wallet.repository.TicketsFormRepository;
import com.wanmi.sbc.wallet.wallet.repository.WalletRecordRepository;
import com.wanmi.sbc.walletorder.bean.enums.PayWalletCallBackResultStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class CustomerWalletMerchantService {


    @Autowired
    private CustomerWalletRepository customerWalletRepository;
    @Autowired
    private PayQueryProvider payQueryProvider;
    @Autowired
    private PayCallBackResultService payCallBackResultService;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private WalletRecordMerchantService walletRecordMerchantService;
    @Autowired
    private TicketsFormMerchantService ticketsFormMerchantService;
    @Autowired
    private GeneratorService generatorService;
    @Autowired
    private StoreQueryProvider storeQueryProvider;
    @Autowired
    private WalletRecordRepository walletRecordRepository;
    @Autowired
    private TicketsFormRepository ticketsFormRepository;
    @Autowired
    private CustomerWalletQueryRepository customerWalletQueryRepository;
    @Autowired
    private CustomerQueryProvider customerQueryProvider;
    @Autowired
    private SystemConfigQueryProvider systemConfigQueryProvider;
    @Autowired
    private TradeProvider tradeProvider;
    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;

    @Autowired
    private CustomerWalletModifyService customerWalletModifyService;

    // 退赠送场景 1. 指定商品返鲸币，2. 商品赠送 3. 充值，4. 收回
    private static final String KING_TZS = "TZS";
    // 赠送场景 1. 指定商品返鲸币，2. 商品赠送 3. 充值，4. 收回
    private static final String KING_ZS = "ZS";
    private static final String KING_CZ = "CZ";
    // 增加鲸币场景 1.下单，2.取消订单
    private static final String KING_ZJ = "ZJ";
    // 扣减鲸币 1.下单，2.取消订单
    private static final String KING_KJ = "KJ";

    private static ExecutorService executor = Executors.newFixedThreadPool(4);

    /**
     * 商户充值鲸币操作customer_wallet
     * */
    @Transactional
    public BaseResponse addCustomerWallet(CustomerWalletPageQueryRequest walletPageQueryRequest) throws Exception {
        try {
            // 1. 检查商户充值的鲸币是否>鲸币账户所剩余额
            // 1.1 查询平台商户所剩鲸币额度
            CustomerWallet customerWallet = customerWalletRepository.findOne(CustomerWalletQueryBuilder.queryByStoreId(AccountUtils.ACCOUNT)).orElse(null);
            if (customerWallet == null) {
                throw new SbcRuntimeException("K-111111","鲸币未初始化,请联系管理员初始化");
            }
            if (customerWallet.getBalance().compareTo(walletPageQueryRequest.getRechargeBalance()) == 0) {
                throw new SbcRuntimeException("K-1111113","平台鲸币不足,管理员正在充值,请稍后再试");
            }
            // 查看用户当前所剩鲸币余额
            CustomerWallet currentUserWallet = customerWalletRepository.findOne(CustomerWalletQueryBuilder.queryByStoreId(customerWallet.getStoreId())).orElse(null);
            currentUserWallet.getBalance().add(walletPageQueryRequest.getRechargeBalance());
            currentUserWallet.getRechargeBalance().add(walletPageQueryRequest.getRechargeBalance());
            CustomerWallet customerWallet1 = customerWalletRepository.saveAndFlush(currentUserWallet);
            // 充值成功
            if (null != customerWallet1) {
                return BaseResponse.SUCCESSFUL();
            }
        } catch (Exception e) {
            throw new SbcRuntimeException("K-111112","充值异常,请联系客服人员处理");
        }
        return null;
    }

    public List<CustomerWallet> queryWallet (CustomerWalletSupplierRequest request) {
        List<CustomerWallet> all = customerWalletRepository.findAll(CustomerWalletSupplierPageBuilder.build(request));
        return all;
    }

    public CustomerWalletStoreIdVO detailStore (WalletByCustomerIdQueryRequest request) {
        CustomerWallet customerWallet = customerWalletRepository.findOne(CustomerWalletQueryBuilder.queryByStoreId(request.getCustomerId())).orElse(null);
        return null;
    }

    @Transactional
    @LcnTransaction
    public void aliPayOnlineCallBack(TradePayWalletOnlineCallBackRequest tradePayOnlineCallBackRequest) throws IOException {
        log.info("===============支付宝回调开始==============");
        GatewayConfigByGatewayRequest gatewayConfigByGatewayRequest = new GatewayConfigByGatewayRequest();
        gatewayConfigByGatewayRequest.setGatewayEnum(PayGatewayEnum.ALIPAY);
        gatewayConfigByGatewayRequest.setStoreId(Constants.BOSS_DEFAULT_STORE_ID);
        //查询支付宝配置信息
        PayGatewayConfigResponse payGatewayConfigResponse =
                payQueryProvider.getGatewayConfigByGateway(gatewayConfigByGatewayRequest).getContext();
        //支付宝公钥
        String aliPayPublicKey = payGatewayConfigResponse.getPublicKey();
        boolean signVerified = false;
        Map<String, String> params =
                JSONObject.parseObject(tradePayOnlineCallBackRequest.getAliPayCallBackResultStr(), Map.class);
        try {
            signVerified = AlipaySignature.rsaCheckV1(params, aliPayPublicKey, "UTF-8", "RSA2"); //调用SDK验证签名
        } catch (AlipayApiException e) {
            log.error("支付宝回调签名校验异常：", e);
        }
        //商户订单号
        String out_trade_no = params.get("out_trade_no");
        // 校验订单合法性
        if (signVerified) {
            try {
                //支付宝交易号
                String trade_no = params.get("trade_no");
                //交易状态
                String trade_status = params.get("trade_status");
                //订单金额
                String total_amount = params.get("total_amount");
                //支付终端类型
                String type = params.get("passback_params");
                if (trade_status.equals("TRADE_SUCCESS")) {
                    // 查询订单
                    WalletRecord walletRecord = walletRecordMerchantService.queryWalletRecordByOrderNo(WalletRecordQueryRequest.builder().recordNo(trade_no).build());
                    String recordNo = walletRecord.getRecordNo();
                    RLock fairLock = redissonClient.getFairLock(recordNo);
                    fairLock.lock();
                    try{
                        // 查询平台鲸币账户
                        CustomerWallet customerWallet = customerWalletRepository.findOne(CustomerWalletQueryBuilder.queryByStoreId(AccountUtils.ACCOUNT)).orElse(null);
                        if (null == customerWallet) {
                            throw new Exception("平台鲸币未初始化，请联系管理员");
                        }
                        BigDecimal total = new BigDecimal(total_amount);
                        if (customerWallet.getBalance().compareTo(total) ==0 || customerWallet.getBalance().compareTo(total)<0) {
                            throw new Exception("平台鲸币不足，请及时充值");
                        }
                        // 查询工单状态
                        TicketsForm ticketsForm = ticketsFormMerchantService.queryTicketByRecordNo(TicketsFormQueryVO.builder().recordNo(trade_no).build());
                        // 处理待审核状态订单
                        if (ticketsForm.getRechargeStatus().equals("1")) {
                            //查询当前用户钱包
                            CustomerWallet userWallet = customerWalletRepository.findOne(CustomerWalletQueryBuilder.queryByWalletId(ticketsForm.getWalletId())).orElse(null);
                            // 扣除平台账户鲸币
                            int i = customerWalletRepository.balancePayOne(AccountUtils.ACCOUNT, ticketsForm.getApplyPrice());
                            // 添加平台扣除流水记录
                            AddWalletRecordRecordRequest build = AddWalletRecordRecordRequest.builder().recordNo(trade_no)
                                    .tradeRemark("用户" + userWallet.getCustomerName() + "充值" + total_amount + "元")
                                    .customerAccount(AccountUtils.ACCOUNT)
                                    .tradeType(WalletRecordTradeType.GIVE)
                                    .budgetType(BudgetType.EXPENDITURE)
                                    .dealPrice(total)
                                    .remark("用户" + userWallet.getCustomerName() + "充值" + total_amount + "元")
                                    .dealTime(LocalDateTime.now())
                                    .storeId(AccountUtils.ACCOUNT)
                                    .currentBalance(customerWallet.getBalance().subtract(total))
                                    .tradeState(TradeStateEnum.PAID).build();
                            WalletRecord context = walletRecordMerchantService.addWalletRecord(build).getContext();
                            // 增加用户鲸币
                            int i1 = customerWalletRepository.addAmountOne(userWallet.getStoreId(), total);
                            // 增加用户流水记录
                            AddWalletRecordRecordRequest build1 = AddWalletRecordRecordRequest.builder().recordNo(trade_no)
                                    .tradeRemark("用户" + userWallet.getCustomerName() + "充值" + total_amount + "元")
                                    .customerAccount(userWallet.getCustomerAccount())
                                    .tradeType(WalletRecordTradeType.RECHARGE)
                                    .budgetType(BudgetType.INCOME)
                                    .dealPrice(total)
                                    .remark("用户" + userWallet.getCustomerName() + "充值" + total_amount + "元")
                                    .dealTime(LocalDateTime.now())
                                    .storeId(userWallet.getStoreId())
                                    .currentBalance(userWallet.getBalance().add(total))
                                    .tradeState(TradeStateEnum.PAID).build();
                            WalletRecord context1 = walletRecordMerchantService.addWalletRecord(build1).getContext();
                            if (i <= 0 || context == null || i1 <=0 || context1 == null) {
                                // 如果有不成功的直接回滚
                                throw new Exception("订单增加的时候出现异常，回滚");
                            }
                            //支付回调处理成功
                            payCallBackResultService.updateStatus(trade_no, PayWalletCallBackResultStatus.SUCCESS);
                        } else {
                            throw new Exception("订单出现异常=========="+ticketsForm.getRechargeStatus());
                        }
                    }catch (Exception e) {
                        fairLock.unlock();
                        throw new Exception("平台鲸币未初始化，请联系管理员");
                    }
                } else {
                    throw new Exception("交易失败支付宝回调状态为=-============="+trade_status);
                }

            } catch (Exception e) {
                log.error("支付宝回调异常：", e);
                payCallBackResultService.updateStatus(out_trade_no, PayWalletCallBackResultStatus.FAILED);
            }
        }
    }

    @Transactional
    @LcnTransaction
    public void wxPayOnlineCallBack(TradePayWalletOnlineCallBackRequest tradePayOnlineCallBackRequest) throws IOException {
        String orderNo="";
        String payOrderNo = "";
        try {
            PayGatewayConfigResponse payGatewayConfig = payQueryProvider.getGatewayConfigByGateway(new
                    GatewayConfigByGatewayRequest(PayGatewayEnum.WECHAT, Constants.BOSS_DEFAULT_STORE_ID)).getContext();
            String apiKey = payGatewayConfig.getApiKey();
            XStream xStream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")));
            xStream.alias("xml", WxPayResultResponse.class);
            WxPayResultResponse wxPayResultResponse =
                    (WxPayResultResponse) xStream.fromXML(tradePayOnlineCallBackRequest.getWxPayCallBackResultStr());
            log.info("-------------微信支付回调,wxPayResultResponse：{}------------", wxPayResultResponse);
            payOrderNo = wxPayResultResponse.getOut_trade_no();
            orderNo = getOrderNoByPayOrderNo(payOrderNo);
            RLock fairLock = redissonClient.getFairLock(payOrderNo);
            fairLock.lock();
            // 执行回调
            try {
                if (wxPayResultResponse.getReturn_code().equals(WXPayConstants.SUCCESS) &&
                        wxPayResultResponse.getResult_code().equals(WXPayConstants.SUCCESS)) {
                    log.info("微信支付异步通知回调状态---成功");
                    // 微信回调参数数据MAP
                    Map<String, String> params =
                            WXPayUtil.xmlToMap(tradePayOnlineCallBackRequest.getWxPayCallBackResultStr());
                    String trade_type = wxPayResultResponse.getTrade_type();
                    // 微信签名校验
                    if (WXPayUtil.isSignatureValid(params, apiKey)) {
                        // 查询订单
                        WalletRecord walletRecord = walletRecordMerchantService.queryWalletRecordByOrderNo(WalletRecordQueryRequest.builder().recordNo(payOrderNo).build());
                        if (null == walletRecord) {
                            // 百分位转换
                            BigDecimal bigDecimal = new BigDecimal(wxPayResultResponse.getTotal_fee()).divide(new BigDecimal(100)).
                                    setScale(2, BigDecimal.ROUND_DOWN);
                            // 添加资金池
                            customerWalletRepository.balancePayOrder(AccountUtils.ACCOUNT,bigDecimal);
                            addWallet(bigDecimal,payOrderNo,wxPayResultResponse.getTransaction_id());
                        }
                    }
                }
            } catch (Exception e) {
                throw new SbcRuntimeException("微信回调异常============"+e);
            }finally {
                fairLock.unlock();
            }
        }catch (Exception e) {
            log.error("微信回调异常：", e);
            payCallBackResultService.updateStatus(payOrderNo, PayWalletCallBackResultStatus.FAILED);
        }
    }

    @Transactional
    @LcnTransaction
    public void ccbPayOnlineCallBack(TradePayWalletOnlineCallBackRequest request) {

        String businessId = request.getBusinessId();
        try {

            RLock fairLock = redissonClient.getFairLock(businessId);
            fairLock.lock();

            try {
                JSONObject obj = JSON.parseObject(request.getCcbPayCallBackResultStr());
                // 支付金额
                String total_amount = obj.getString("Ordr_Amt");
                log.info("鲸币充值回调成功，业务ID:{}, 充值金额：{}", businessId, total_amount);
                // 支付流水号
                String pyTrnNo = obj.getString("Py_Trn_No");

                // 查询订单
                WalletRecord walletRecord = walletRecordMerchantService.queryWalletRecordByOrderNo(WalletRecordQueryRequest.builder().recordNo(businessId).build());
                if (null == walletRecord) {
                    BigDecimal bigDecimal = new BigDecimal(total_amount);
                    customerWalletRepository.balancePayOrder(AccountUtils.ACCOUNT,bigDecimal);
                    addWallet(bigDecimal, businessId, pyTrnNo);
                }
            } catch (Exception e) {
                throw new SbcRuntimeException("建行鲸币充值回调异常============", e);
            }finally {
                fairLock.unlock();
            }
        }catch (Exception e) {
            log.error("微信回调异常：", e);
            payCallBackResultService.updateStatus(businessId, PayWalletCallBackResultStatus.FAILED);
        }
    }

    @Transactional
    @LcnTransaction
    public void addWallet(BigDecimal total,String payOrderNo,String transaction_id) {
        try {
            // 查询平台鲸币账户
            CustomerWallet customerWallet = customerWalletRepository.findOne(CustomerWalletQueryBuilder.queryByStoreId(AccountUtils.ACCOUNT)).orElse(null);
            if (null == customerWallet) {
                throw new SbcRuntimeException("平台鲸币未初始化，请联系管理员");
            }
            if (customerWallet.getBalance().compareTo(total) == 0 || customerWallet.getBalance().compareTo(total) < 0) {
                throw new SbcRuntimeException("平台鲸币不足，请及时充值");
            }

            // 查询工单状态
            TicketsForm ticketsForm = ticketsFormMerchantService.queryTicketByRecordNo(TicketsFormQueryVO.builder().recordNo(payOrderNo).build());
            // 处理待审核状态订单
            if (ticketsForm.getRechargeStatus() == 1) {
                //查询当前用户钱包
                CustomerWallet userWallet = customerWalletRepository.findOne(CustomerWalletQueryBuilder.queryByWalletId(ticketsForm.getWalletId())).orElse(null);
                String companyCodeNew = storeQueryProvider.getById(StoreByIdRequest.builder().storeId(Long.valueOf(userWallet.getStoreId())).build())
                        .getContext().getStoreVO().getCompanyInfo().getCompanyCodeNew();
                // 扣除平台账户鲸币
                int i = customerWalletRepository.balancePayOne(AccountUtils.ACCOUNT, ticketsForm.getApplyPrice());
                // 添加平台扣除流水记录
                String w = generatorService.generate("W");
                AddWalletRecordRecordRequest build = AddWalletRecordRecordRequest.builder().recordNo(w)
                        .tradeRemark(WalletDetailsType.STORE_GO.getDesc()+"-"+transaction_id)
                        .customerAccount(AccountUtils.CUSTOMER_ACCOUNT)
                        .tradeType(WalletRecordTradeType.GIVE)
                        .budgetType(BudgetType.EXPENDITURE)
                        .dealPrice(total)
                        .remark(WalletDetailsType.STORE_GO.getDesc())
                        .dealTime(LocalDateTime.now())
                        .currentBalance(customerWallet.getBalance().subtract(total))
                        .payType(1)
                        .relationOrderId(transaction_id)
                        .storeId(AccountUtils.ACCOUNT)
                        .tradeState(TradeStateEnum.PAID).build();
                WalletRecord context = walletRecordMerchantService.addWalletRecord(build).getContext();
                // 推送金蝶单子--平台扣除鲸币推送收款退款单
                if (context != null) {
                    WalletRecord walletRecord = new WalletRecord();
                    KsBeanUtil.copyProperties(context,walletRecord);
                    walletRecord.setCustomerAccount("BJ-0000000");
                    pushKingDeeTZS(KsBeanUtil.convert(context,WalletRecordVO.class),transaction_id,KING_TZS);
                }
                // 增加用户鲸币
                int i1 = customerWalletRepository.addAmountOne(userWallet.getStoreId(), total);
                // 增加用户流水记录
                AddWalletRecordRecordRequest build1 = AddWalletRecordRecordRequest.builder().recordNo(payOrderNo)
                        .tradeRemark(WalletDetailsType.STORE_GO.getDesc()+"-"+transaction_id)
                        .customerAccount(userWallet.getCustomerAccount())
                        .tradeType(WalletRecordTradeType.RECHARGE)
                        .budgetType(BudgetType.INCOME)
                        .dealPrice(total)
                        .remark(WalletDetailsType.STORE_GO.getDesc())
                        .dealTime(LocalDateTime.now())
                        .currentBalance(userWallet.getBalance().add(total))
                        .payType(1)
                        .relationOrderId(transaction_id)
                        .storeId(userWallet.getStoreId())
                        .tradeState(TradeStateEnum.PAID).build();
                WalletRecord context1 = walletRecordMerchantService.addWalletRecord(build1).getContext();
                if (i <= 0 || context == null || i1 <= 0 || context1 == null) {
                    // 如果有不成功的直接回滚
                    throw new SbcRuntimeException("K-000001","订单增加的时候出现异常，回滚");
                }
                //商户增加鲸币--推送收款单
                WalletRecordVO walletRecordVO = new WalletRecordVO();
                KsBeanUtil.copyProperties(context1,walletRecordVO);
                walletRecordVO.setCustomerAccount(companyCodeNew);
                pushKingDeeZS(walletRecordVO,transaction_id,KING_CZ);
                //刷新工单状态
                ticketsFormMerchantService.updateTicketsForm(ticketsForm.getRecordNo(),2);
                SystemConfigResponse response =
                        systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                                .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
                JSONObject jsonObject = JSONObject.parseObject(   response.getSystemConfigVOList().get(0).getContext());
                String appKey= jsonObject.getString("appKey");
                long appId= jsonObject.getLong("appId");
                TencentImCustomerUtil.sendSingleChat(userWallet.getCustomerAccount(), "", appId, appKey);
            }
        } catch (Exception e) {
            throw new SbcRuntimeException("充值订单重复，请检查");
        }
    }

    @Transactional
    @LcnTransaction
    public void addWallet(BigDecimal total,String payOrderNo ) throws Exception {
        try {
            log.info("进入账户钱包=========");
            // 查询平台鲸币账户
            CustomerWallet customerWallet = customerWalletRepository.findOne(CustomerWalletQueryBuilder.queryByStoreId(AccountUtils.ACCOUNT)).orElse(null);
            if (null == customerWallet) {
                throw new SbcRuntimeException("平台鲸币未初始化，请联系管理员");
            }
            if (customerWallet.getBalance().compareTo(total) == 0 || customerWallet.getBalance().compareTo(total) < 0) {
                throw new SbcRuntimeException("平台鲸币不足，请及时充值");
            }
            log.info("查询出来账户===={}",customerWallet);
            // 查询工单状态
            TicketsForm ticketsForm = ticketsFormMerchantService.queryTicketByRecordNo(TicketsFormQueryVO.builder().recordNo(payOrderNo).build());
            log.info("交易号====={}",ticketsForm);
            // 处理待审核状态订单
            if (ticketsForm.getRechargeStatus() == 1) {
                //查询当前用户钱包
                CustomerWallet userWallet = customerWalletRepository.findOne(CustomerWalletQueryBuilder.queryByWalletId(ticketsForm.getWalletId())).orElse(null);
                String companyCodeNew = storeQueryProvider.getById(StoreByIdRequest.builder().storeId(Long.valueOf(userWallet.getStoreId())).build())
                        .getContext().getStoreVO().getCompanyInfo().getCompanyCodeNew();

                // 扣除平台账户鲸币
                int i = customerWalletRepository.balancePayOne(AccountUtils.ACCOUNT, ticketsForm.getApplyPrice());
                // 添加平台扣除流水记录
                String w = generatorService.generate("W");
                AddWalletRecordRecordRequest build = AddWalletRecordRecordRequest.builder().recordNo(w)
                        .tradeRemark(WalletDetailsType.PLATO_GO.getDesc()+"-"+w)
                        .customerAccount(customerWallet.getCustomerAccount())
                        .tradeType(WalletRecordTradeType.GIVE)
                        .budgetType(BudgetType.EXPENDITURE)
                        .dealPrice(total)
                        .remark(WalletDetailsType.PLATO_GO.getDesc())
                        .dealTime(LocalDateTime.now())
                        .currentBalance(customerWallet.getBalance().subtract(total))
                        .payType(1)
                        .relationOrderId(payOrderNo)
                        .storeId(customerWallet.getStoreId())
                        .tradeState(TradeStateEnum.PAID).build();
                WalletRecord context = walletRecordMerchantService.addWalletRecord(build).getContext();
                // 推送金蝶单子--用户减少鲸币推送收款退款单
                if (context != null) {
                    WalletRecord walletRecord = new WalletRecord();
                    KsBeanUtil.copyProperties(context,walletRecord);
                    walletRecord.setCustomerAccount("BJ-0000000");
                    pushKingDeeTZS(KsBeanUtil.convert(context,WalletRecordVO.class),payOrderNo,KING_TZS);
                }
                // 增加用户鲸币
                int i1 = customerWalletRepository.addAmountOne(userWallet.getStoreId(), total);
                // 增加用户流水记录
                AddWalletRecordRecordRequest build1 = AddWalletRecordRecordRequest.builder().recordNo(payOrderNo)
                        .tradeRemark(WalletDetailsType.PLATO_GO.getDesc()+"-"+payOrderNo)
                        .customerAccount(userWallet.getCustomerAccount())
                        .tradeType(WalletRecordTradeType.RECHARGE)
                        .budgetType(BudgetType.INCOME)
                        .dealPrice(total)
                        .remark(WalletDetailsType.PLATO_GO.getDesc())
                        .dealTime(LocalDateTime.now())
                        .currentBalance(userWallet.getBalance().add(total))
                        .payType(1)
                        .relationOrderId(payOrderNo)
                        .storeId(userWallet.getStoreId())
                        .tradeState(TradeStateEnum.PAID).build();
                WalletRecord context1 = walletRecordMerchantService.addWalletRecord(build1).getContext();
                if (i <= 0 || context == null || i1 <= 0 || context1 == null) {
                    // 如果有不成功的直接回滚
                    throw new SbcRuntimeException("K-000001","订单增加的时候出现异常，回滚");
                }
                WalletRecordVO walletRecordVO = new WalletRecordVO();
                KsBeanUtil.copyProperties(context1,walletRecordVO);
                walletRecordVO.setCustomerAccount(companyCodeNew);
                pushKingDeeZS(walletRecordVO,payOrderNo,KING_ZS);
                ticketsFormMerchantService.updateTicketsForm(ticketsForm.getRecordNo(),2);
            }
        } catch (Exception e) {
            log.info("订单异常======{}", ExceptionUtils.getMessage(e));
            throw new SbcRuntimeException("充值订单重复，请检查");
        }
    }
    private String getOrderNoByPayOrderNo(String payOrderNo){
        PayTradeRecordRequest recordRequest = new PayTradeRecordRequest();
        recordRequest.setPayOrderNo(payOrderNo);
        PayTradeRecordResponse payOrderResponse =  payQueryProvider.findByPayOrderNo(recordRequest).getContext();
        return payOrderResponse.getBusinessId();
    }

    public CustomerWallet getWalletByStoreId(String storeId) {

        return customerWalletRepository.findOne(CustomerWalletQueryBuilder.queryByStoreId(storeId)).orElse(null);
    }

    @Transactional(noRollbackFor = SbcRuntimeException.class)
    @LcnTransaction
    public BaseResponse<WalletRecordVO> orderByGiveStore(CustomerWalletOrderByRequest request) {

        if (StringUtils.isEmpty(request.getStoreId())) {
            throw new SbcRuntimeException("K-111113", "请选择一个商户");
        }
        // 查询商户钱包
        CustomerWallet customerWallet = customerWalletRepository.findOne(CustomerWalletQueryBuilder.queryByStoreId(request.getStoreId())).orElse(null);
        log.info("查询customerWallet");
        if (null == customerWallet) {
            customerWallet = addStoreCustomer(request.getStoreId());
            if (null == customerWallet) {
                throw new SbcRuntimeException("K-111113", "当前商户未开通钱包权限");
            }
        }
        StoreByIdRequest storeByIdRequest = new StoreByIdRequest();
        storeByIdRequest.setStoreId(Long.valueOf(request.getStoreId()));
        StoreVO storeVO = storeQueryProvider.getById(storeByIdRequest).getContext().getStoreVO();
        CompanyType companyType = storeVO.getCompanyType();
        Boolean companyTypeFlag = false;
        if (companyType.toValue() == CompanyType.PLATFORM.toValue() || companyType.toValue() == CompanyType.UNIFIED.toValue()) {
            companyTypeFlag = true;
        }

        log.info("当前账号=========={}", request.getCustomerAccount());
        if (!StringUtils.isEmpty(request.getCustomerAccount())) {
            NoDeleteCustomerGetByAccountRequest accountRequest = new NoDeleteCustomerGetByAccountRequest();
            accountRequest.setCustomerAccount(request.getCustomerAccount());
            NoDeleteCustomerGetByAccountResponse context = customerQueryProvider.getNoDeleteCustomerByAccount(accountRequest).getContext();
            log.info("当前用户Id======{}", context.getCustomerId());
            if (null == context) {
                throw new SbcRuntimeException("K-0000001", "未找到当前用户钱包");
            }
            request.setCustomerId(context.getCustomerId());
        }
        if (StringUtils.isEmpty(request.getCustomerId())) {
            throw new SbcRuntimeException("K-0000001", "未找到当前用户钱包");
        }
        CustomerWallet customerWalletByCustomerId = customerWalletRepository.getCustomerWalletByCustomerId(request.getCustomerId());
        if (customerWalletByCustomerId == null) {
            customerWalletByCustomerId = addUserCustomer(request.getCustomerId(), request.getCustomerAccount());
            if (null == customerWalletByCustomerId) {
                throw new SbcRuntimeException("K-111113", "当前用户未开通钱包权限");
            }
        }
        // 赠送鲸币
        BigDecimal balance = request.getBalance();
        if (customerWalletByCustomerId.getBalance().compareTo(balance) < 0) {
            throw new SbcRuntimeException("K-111113", "余额不足，请充值鲸币在购买");
        }
        String companyCodeNew = storeQueryProvider.getById(StoreByIdRequest.builder().storeId(Long.valueOf(customerWallet.getStoreId())).build())
                .getContext().getStoreVO().getCompanyInfo().getCompanyCodeNew();

        // 收回业务
        RLock fairLock = redissonClient.getFairLock(request.getCustomerId());
        fairLock.lock();
        try {
            // 增加商家鲸币
            int i = customerWalletRepository.balancePayOrder(customerWallet.getStoreId(), balance);
            if (i > 0) {
                // 记录流水
                // 添加商户增加流水记录
                String recordNo = generatorService.generate("J");
                AddWalletRecordRecordRequest build = AddWalletRecordRecordRequest.builder().recordNo(recordNo)
                        //"商户" + customerWallet.getCustomerName() + "赠送" + balance + "元,给用户"+customerWalletByCustomerId.getCustomerName()
                        .tradeRemark(request.getTradeRemark())
                        .customerAccount(customerWallet.getCustomerAccount())
                        .tradeType(request.getWalletRecordTradeType())
                        .budgetType(BudgetType.INCOME)
                        .dealPrice(balance)
                        .remark(request.getRemark())
                        .dealTime(LocalDateTime.now())
                        .currentBalance(customerWallet.getBalance().add(balance))
                        .relationOrderId(request.getRelationOrderId())
                        .storeId(customerWallet.getStoreId())
                        .tradeState(TradeStateEnum.PAID).build();
                WalletRecord context = walletRecordMerchantService.addWalletRecord(build).getContext();
                Assert.notNull(context, "商户账户增加流水记录异常！");
                // 商家加鲸币推送收款单
                WalletRecordVO walletRecordVO = new WalletRecordVO();
                KsBeanUtil.copyProperties(context,walletRecordVO);
                walletRecordVO.setCustomerAccount(companyCodeNew);
                if (companyTypeFlag) {
                    log.info("异步推送金蝶开始======");
                    executor.submit(() -> pushKingDeeZS(walletRecordVO,request.getRelationOrderId(),KING_ZJ));
                }
                if (context != null) {
                    int i1 = customerWalletRepository.balancePay(customerWalletByCustomerId.getCustomerId(), balance);
                    if (i1 > 0) {
                        // 减少用户鲸币数量
                        String recordNo1 = generatorService.generate("J");
                        AddWalletRecordRecordRequest build1 = AddWalletRecordRecordRequest.builder().recordNo(recordNo1)
                                .tradeRemark(request.getTradeRemark())
                                .customerAccount(customerWalletByCustomerId.getCustomerAccount())
                                .tradeType(request.getWalletRecordTradeType())
                                .budgetType(BudgetType.EXPENDITURE)
                                .dealPrice(balance)
                                .remark(request.getRemark())
                                .dealTime(LocalDateTime.now())
                                .relationOrderId(request.getRelationOrderId())
                                .storeId(customerWalletByCustomerId.getStoreId())
                                .currentBalance(customerWalletByCustomerId.getBalance().subtract(balance))
                                .tradeState(TradeStateEnum.PAID).build();
                        WalletRecord context1 = walletRecordMerchantService.addWalletRecord(build1).getContext();
                        Assert.notNull(context1, "添加记录失败！");
                        if (null != context1) {
                            WalletRecordVO walletRecordVO1 = new WalletRecordVO();
                            KsBeanUtil.copyProperties(context1,walletRecordVO1);
                            // 用户减少鲸币--推送收款退款单 用户下单给商家，用户增加鲸币需要推送给商家其他的不用给
                            log.info("异步推送金蝶开始111======");
                            executor.submit(() -> pushKingDeeTZS(walletRecordVO1,request.getRelationOrderId(),KING_TZS));
                            if (companyTypeFlag) {
                                executor.submit(() -> pushKingDeeZS(walletRecordVO1,request.getRelationOrderId(),KING_ZS));
                            }
                            log.info("返回出去的对象==={}",walletRecordVO1.toString());
                            return BaseResponse.success(walletRecordVO1);
                        }
                    }
                }
            } else {
                throw new SbcRuntimeException("K-111114", "商户增加鲸币异常");
            }
        } catch (Exception e) {
            throw new SbcRuntimeException(e);
        } finally {
            fairLock.unlock();
        }
        return BaseResponse.FAILED();
    }
    /**
     * opertionType
     * 0 商家鲸币减少，用户鲸币增加
     * 1 用户鲸币增加，商户鲸币减少
     * */
    @LcnTransaction
    @Transactional(noRollbackFor = SbcRuntimeException.class)
    public BaseResponse<WalletRecordVO> merchantGiveUser(CustomerWalletGiveRequest customerWalletGiveRequest) throws SbcRuntimeException {
        // 查询当前商户钱包
        if ( StringUtils.isEmpty(customerWalletGiveRequest.getStoreId())) {
            throw new SbcRuntimeException("K-111113","请选择一个商户");
        }
        StoreByIdRequest storeByIdRequest = new StoreByIdRequest();
        storeByIdRequest.setStoreId(Long.valueOf(customerWalletGiveRequest.getStoreId()));
        StoreVO storeVO = storeQueryProvider.getById(storeByIdRequest).getContext().getStoreVO();
        CompanyType companyType = storeVO.getCompanyType();
        Boolean companyTypeFlag = false;
        if (companyType.toValue() == CompanyType.PLATFORM.toValue() || companyType.toValue() == CompanyType.UNIFIED.toValue()) {
            companyTypeFlag = true;
        }
        // 查询商户钱包
        CustomerWallet customerWallet = customerWalletRepository.findOne(CustomerWalletQueryBuilder.queryByStoreId(customerWalletGiveRequest.getStoreId())).orElse(null);
        if (null == customerWallet) {
            customerWallet = addStoreCustomer(customerWalletGiveRequest.getStoreId());
            if (null == customerWallet  || customerWallet.getIsEnable() == 0) {
                throw new SbcRuntimeException("K-111113","当前商户未开通钱包权限");
            }
        }
        log.info("当前账号=========={}",customerWalletGiveRequest.getCustomerAccount());
        if (!StringUtils.isEmpty(customerWalletGiveRequest.getCustomerAccount())) {
            NoDeleteCustomerGetByAccountRequest accountRequest = new NoDeleteCustomerGetByAccountRequest();
            accountRequest.setCustomerAccount(customerWalletGiveRequest.getCustomerAccount());
            NoDeleteCustomerGetByAccountResponse context = customerQueryProvider.getNoDeleteCustomerByAccount(accountRequest).getContext();
            log.info("当前用户Id======{}",context.getCustomerId());
            if (null == context) {
                throw new SbcRuntimeException("K-0000001","未找到当前用户钱包");
            }
            customerWalletGiveRequest.setCustomerId(context.getCustomerId());
        }
        // 查询转账钱包
        CustomerWallet customerWalletByCustomerId = null;
        String changOverToCodeNew = null;
        if (!StringUtils.isEmpty(customerWalletGiveRequest.getChangOverToStoreAccount() )) {
            // 根据账号是否存在
            EmployeeVO employee = employeeQueryProvider.getByAccountName(EmployeeByAccountNameRequest.builder()
                    .accountName(customerWalletGiveRequest.getChangOverToStoreAccount()).accountType(AccountType.s2bSupplier).build()).getContext().getEmployee();
            if (employee == null) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"未找到商家");
            }
            log.info("公司ID========={}",employee.getCompanyInfo());
            StoreVO storeVO1 = storeQueryProvider.getStoreByCompanyInfoId(StoreByCompanyInfoIdRequest.builder().companyInfoId(employee.getCompanyInfoId()).build()).getContext().getStoreVO();
            log.info("商家ID========{}",storeVO1.getStoreId());
            customerWalletByCustomerId = customerWalletRepository.findOne(CustomerWalletQueryBuilder.queryByStoreId(String.valueOf(storeVO1.getStoreId()))).orElse(null);
            changOverToCodeNew= storeVO1.getCompanyInfo().getCompanyCodeNew();
            if (null == customerWalletByCustomerId) {
                customerWalletByCustomerId = addStoreCustomer(storeVO1.getStoreId().toString());
            }
        } else {
            customerWalletByCustomerId =customerWalletRepository.getCustomerWalletByCustomerId(customerWalletGiveRequest.getCustomerId());
            if (null == customerWalletByCustomerId) {
                customerWalletByCustomerId = addUserCustomer(customerWalletGiveRequest.getCustomerId(), customerWalletByCustomerId.getCustomerAccount());
            }
        }

        if (customerWalletByCustomerId == null ) {
            throw new SbcRuntimeException("K-111113","当前用户未开通钱包权限");
        }
        // 赠送鲸币
        BigDecimal balance = customerWalletGiveRequest.getBalance();
        if (customerWallet.getBalance().compareTo(balance) <0 && customerWalletGiveRequest.getOpertionType() == 0) {
            throw new SbcRuntimeException("K-111113","余额不足，请充值鲸币在赠送");
        }
        if (customerWalletByCustomerId.getBalance().compareTo(balance) < 0 && customerWalletGiveRequest.getOpertionType() == 1) {
            throw new SbcRuntimeException("K-111113","用户鲸币不够，无法收回");
        }
        String companyCodeNew = storeQueryProvider.getById(StoreByIdRequest.builder().storeId(Long.valueOf(customerWallet.getStoreId())).build())
                .getContext().getStoreVO().getCompanyInfo().getCompanyCodeNew();

        // 收回业务
        RLock fairLock = redissonClient.getFairLock(customerWallet.getWalletId().toString());
        fairLock.lock();
        try {
            if (customerWalletGiveRequest.getOpertionType() == 0) {
                // 扣除商家鲸币
                int i = customerWalletRepository.balancePayOne(customerWallet.getStoreId(), balance);
                log.info("获取商家ID====={}",customerWallet.getStoreId());
                if (i > 0) {
                    // 记录流水
                    // 添加商户扣除流水记录
                    String recordNo = generatorService.generate("G");
                    AddWalletRecordRecordRequest build = AddWalletRecordRecordRequest.builder().recordNo(recordNo)
                            //"商户" + customerWallet.getCustomerName() + "赠送" + balance + "元,给用户"+customerWalletByCustomerId.getCustomerName()
                            .tradeRemark(customerWalletGiveRequest.getTradeRemark())
                            .customerAccount(customerWallet.getCustomerAccount())
                            .tradeType(customerWalletGiveRequest.getWalletRecordTradeType() != null?customerWalletGiveRequest.getWalletRecordTradeType():WalletRecordTradeType.GIVE)
                            .budgetType(BudgetType.EXPENDITURE)
                            .dealPrice(balance)
                            .remark(customerWalletGiveRequest.getRemark())
                            .dealTime(LocalDateTime.now())
                            .currentBalance(customerWallet.getBalance().subtract(balance))
                            .relationOrderId(customerWalletGiveRequest.getRelationOrderId())
                            .storeId(customerWallet.getStoreId())
                            .tradeState(TradeStateEnum.PAID).build();
                    WalletRecord context = walletRecordMerchantService.addWalletRecord(build).getContext();
                    Assert.notNull(context,"商户账户增加流水记录异常！");
                    WalletRecordVO walletRecordVO = new WalletRecordVO();
                    KsBeanUtil.copyProperties(context,walletRecordVO);
                    walletRecordVO.setCustomerAccount(companyCodeNew);
                    // 推送金蝶信息 商家减鲸币推送收款退款单 平台自营的才推送金蝶
                    if (companyTypeFlag) {
                        executor.submit(() ->pushKingDeeTZS(walletRecordVO,customerWalletGiveRequest.getRelationOrderId(),KING_KJ));
                    }
                    if (context != null ) {
                        int i1 = customerWalletRepository.addAmountUser(customerWalletByCustomerId.getCustomerId(), balance);
                        log.info("获取商家ID1====={}",customerWalletByCustomerId.getStoreId());
                        if (i1 > 0) {
                            // 增加用户鲸币数量
                            String recordNo1 = generatorService.generate("G");
                            AddWalletRecordRecordRequest build1 = AddWalletRecordRecordRequest.builder().recordNo(recordNo1)
                                    .tradeRemark(customerWalletGiveRequest.getTradeRemark())
                                    .customerAccount(customerWalletByCustomerId.getCustomerAccount())
                                    .tradeType(customerWalletGiveRequest.getWalletRecordTradeType() != null ?customerWalletGiveRequest.getWalletRecordTradeType():WalletRecordTradeType.RECHARGE)
                                    .budgetType(BudgetType.INCOME)
                                    .dealPrice(balance)
                                    .remark(customerWalletGiveRequest.getRemark())
                                    .dealTime(LocalDateTime.now())
                                    .currentBalance(customerWalletByCustomerId.getBalance().add(balance))
                                    .relationOrderId(customerWalletGiveRequest.getRelationOrderId())
                                    .storeId(customerWalletByCustomerId.getStoreId())
                                    .tradeState(TradeStateEnum.PAID).build();
                            WalletRecord context1 = walletRecordMerchantService.addWalletRecord(build1).getContext();
                            Assert.notNull(context1, "添加记录失败！");
                            if (null != context1) {
                                WalletRecordVO walletRecordVO1 = new WalletRecordVO();
                                KsBeanUtil.copyProperties(context1,walletRecordVO1);
                                walletRecordVO1.setCustomerAccount(StringUtils.isEmpty(changOverToCodeNew)? context1.getCustomerAccount() : changOverToCodeNew);
                                // 推送金蝶--用户增加鲸币推送收款单
                                pushKingDeeZS(walletRecordVO1, customerWalletGiveRequest.getRelationOrderId(), KING_ZS);
                                if (companyTypeFlag) {
                                    if (customerWalletGiveRequest.getWalletRecordTradeType() == WalletRecordTradeType.BALANCE_REFUND) {
                                        executor.submit(() ->pushKingDeeTZS(walletRecordVO1, customerWalletGiveRequest.getRelationOrderId(), KING_TZS));
                                    }
                                }
                                log.info("返回出去的对象==={}",walletRecordVO1.toString());
                                return BaseResponse.success(walletRecordVO1);
                            }
                        }
                    }
                } else{
                    throw new SbcRuntimeException("K-111114","商户增加鲸币异常");
                }
            } else {
                // 收回鲸币
                int i = customerWalletRepository.addAmountOne(customerWallet.getStoreId(), balance);
                if (i > 0) {
                    // 记录流水
                    // 添加商户扣除流水记录
                    String recordNo = generatorService.generate("G");
                    AddWalletRecordRecordRequest build = AddWalletRecordRecordRequest.builder().recordNo(recordNo)
                            //"商户" + customerWallet.getCustomerName() + "赠送" + balance + "元,给用户"+customerWalletByCustomerId.getCustomerName()
                            .tradeRemark(customerWalletGiveRequest.getTradeRemark())
                            .customerAccount(customerWallet.getCustomerAccount())
                            .tradeType(customerWalletGiveRequest.getWalletRecordTradeType())
                            .budgetType(BudgetType.INCOME)
                            .dealPrice(balance)
                            .remark(customerWalletGiveRequest.getRemark())
                            .dealTime(LocalDateTime.now())
                            .currentBalance(customerWallet.getBalance().add(balance))
                            .relationOrderId(customerWalletGiveRequest.getRelationOrderId())
                            .storeId(customerWallet.getStoreId())
                            .tradeState(TradeStateEnum.PAID).build();
                    WalletRecord context = walletRecordMerchantService.addWalletRecord(build).getContext();
                    Assert.notNull(context,"商户账户增加流水记录异常！");
                    // 商家加鲸币推送收款单
                    WalletRecordVO walletRecordVO = new WalletRecordVO();
                    KsBeanUtil.copyProperties(context,walletRecordVO);
                    walletRecordVO.setCustomerAccount(companyCodeNew);
                    executor.submit(() ->pushKingDeeZS(walletRecordVO,customerWalletGiveRequest.getRelationOrderId(),KING_ZJ));
                    int i1 = customerWalletRepository.balancePay(customerWalletByCustomerId.getCustomerId(), balance);
                    if (i1 > 0) {
                        // 增加用户鲸币数量
                        String recordNo1 = generatorService.generate("G");
                        AddWalletRecordRecordRequest build1 = AddWalletRecordRecordRequest.builder().recordNo(recordNo1)
                                .tradeRemark(customerWalletGiveRequest.getTradeRemark())
                                .customerAccount(customerWalletByCustomerId.getCustomerAccount())
                                .tradeType(customerWalletGiveRequest.getWalletRecordTradeType())
                                .budgetType(BudgetType.EXPENDITURE)
                                .dealPrice(balance)
                                .remark(customerWalletGiveRequest.getRemark())
                                .dealTime(LocalDateTime.now())
                                .relationOrderId(customerWalletGiveRequest.getRelationOrderId())
                                .currentBalance(customerWalletByCustomerId.getBalance().subtract(balance))
                                .storeId(customerWalletByCustomerId.getStoreId())
                                .tradeState(TradeStateEnum.PAID).build();
                        WalletRecord context1 = walletRecordMerchantService.addWalletRecord(build1).getContext();
                        if (context1 != null) {
                            WalletRecordVO walletRecordVO1 = new WalletRecordVO();
                            KsBeanUtil.copyProperties(context1,walletRecordVO1);
                            // 用户减少鲸币--推送收款退款单,用户下单减少鲸币不用推送到ERP
                            if (companyTypeFlag) {
                                executor.submit(() ->pushKingDeeTZS(walletRecordVO1, customerWalletGiveRequest.getRelationOrderId(), KING_TZS));
                            }
                            log.info("返回出去的对象==={}",walletRecordVO1.toString());
                            return BaseResponse.success(walletRecordVO1);
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new SbcRuntimeException(e);
        }finally {
            fairLock.unlock();
        }
        return null;
    }

    // 平台鲸币充值不走微信支付方式
    public void platRecharge(CustomerWalletOrderByRequest customerWalletOrderByRequest) {
        // 添加充值记录
        // 查询平台的充值账号
        CustomerWallet customerWallet = customerWalletRepository.findOne(CustomerWalletQueryBuilder.queryByStoreId(AccountUtils.ACCOUNT)).orElse(null);
        String o = generatorService.generate("CZ");
        AddWalletRecordRecordRequest build1 = AddWalletRecordRecordRequest.builder().recordNo(o)
                .tradeRemark(WalletDetailsType.PLATO_GO.getDesc()+"-"+o)
                .customerAccount(AccountUtils.CUSTOMER_ACCOUNT)
                .tradeType(WalletRecordTradeType.RECHARGE)
                .budgetType(BudgetType.INCOME)
                .dealPrice(customerWalletOrderByRequest.getBalance())
                .remark(WalletDetailsType.PLATO_GO.getDesc())
                .dealTime(LocalDateTime.now())
                .relationOrderId(o)
                .currentBalance(customerWallet.getBalance().subtract(customerWalletOrderByRequest.getBalance()))
                .storeId(customerWallet.getStoreId())
                .tradeState(TradeStateEnum.PAID).build();
        WalletRecord context1 = walletRecordMerchantService.addWalletRecord(build1).getContext();
        if (context1 != null) {
            int i = customerWalletRepository.addAmountOne(customerWallet.getStoreId(), customerWalletOrderByRequest.getBalance());
            if (i > 0) {
                WalletRecord walletRecord = new WalletRecord();
                KsBeanUtil.copyProperties(context1,walletRecord);
                walletRecord.setCustomerAccount("BJ-0000000");
                executor.submit(() ->pushKingDeeZS(KsBeanUtil.convert(context1,WalletRecordVO.class),o,KING_ZS));
            }
        }
    }

//
//    @LcnTransaction
//    @Transactional(noRollbackFor = SbcRuntimeException.class)
//    public BaseResponse<WalletRecordVO> merchantGiveUserPrivate(CustomerWalletGiveRequest customerWalletGiveRequest) throws SbcRuntimeException {
//        // 查询当前商户钱包
//        if ( StringUtils.isEmpty(customerWalletGiveRequest.getStoreId())) {
//            throw new SbcRuntimeException("K-111113","请选择一个商户");
//        }
//        // 查询商户钱包
//        CustomerWallet customerWallet = customerWalletRepository.findOne(CustomerWalletQueryBuilder.queryByStoreId(customerWalletGiveRequest.getStoreId())).orElse(null);
//        if (null == customerWallet || customerWallet.getIsEnable() == 0) {
//            throw new SbcRuntimeException("K-111113","当前商户未开通钱包权限");
//        }
//        log.info("当前账号=========={}",customerWalletGiveRequest.getCustomerAccount());
//        if (!StringUtils.isEmpty(customerWalletGiveRequest.getCustomerAccount())) {
//            NoDeleteCustomerGetByAccountRequest accountRequest = new NoDeleteCustomerGetByAccountRequest();
//            accountRequest.setCustomerAccount(customerWalletGiveRequest.getCustomerAccount());
//            NoDeleteCustomerGetByAccountResponse context = customerQueryProvider.getNoDeleteCustomerByAccount(accountRequest).getContext();
//            log.info("当前用户Id======{}",context.getCustomerId());
//            if (null == context) {
//                throw new SbcRuntimeException("K-0000001","未找到当前用户钱包");
//            }
//            customerWalletGiveRequest.setCustomerId(context.getCustomerId());
//        }
//        if (StringUtils.isEmpty(customerWalletGiveRequest.getCustomerId())) {
//            throw new SbcRuntimeException("K-0000001","未找到当前用户钱包");
//        }
//        // 查询用户钱包
//        CustomerWallet customerWalletByCustomerId = customerWalletRepository.getCustomerWalletByCustomerId(customerWalletGiveRequest.getCustomerId());
//        if (customerWalletByCustomerId == null ) {
//            throw new SbcRuntimeException("K-111113","当前用户未开通钱包权限");
//        }
//        // 赠送鲸币
//        BigDecimal balance = customerWalletGiveRequest.getBalance();
//        if (customerWallet.getBalance().compareTo(balance) <0 && customerWalletGiveRequest.getOpertionType() == 0) {
//            throw new SbcRuntimeException("K-111113","余额不足，请充值鲸币在赠送");
//        }
//        if (customerWalletByCustomerId.getBalance().compareTo(balance) < 0 && customerWalletGiveRequest.getOpertionType() == 1) {
//            throw new SbcRuntimeException("K-111113","用户鲸币不够，无法收回");
//        }
//        String companyCodeNew = storeQueryProvider.getById(StoreByIdRequest.builder().storeId(Long.valueOf(customerWallet.getStoreId())).build())
//                .getContext().getStoreVO().getCompanyInfo().getCompanyCodeNew();
//        // 收回业务
//        RLock fairLock = redissonClient.getFairLock(customerWallet.getStoreId());
//        fairLock.lock();
//        try {
//            if (customerWalletGiveRequest.getOpertionType() == 0) {
//                // 扣除商家鲸币
//                int i = customerWalletRepository.balancePayOne(customerWallet.getStoreId(), balance);
//                if (i > 0) {
//                    // 记录流水
//                    // 添加商户扣除流水记录
//                    String recordNo = generatorService.generate("G");
//                    AddWalletRecordRecordRequest build = AddWalletRecordRecordRequest.builder().recordNo(recordNo)
//                            //"商户" + customerWallet.getCustomerName() + "赠送" + balance + "元,给用户"+customerWalletByCustomerId.getCustomerName()
//                            .tradeRemark(customerWalletGiveRequest.getTradeRemark())
//                            .customerAccount(customerWallet.getCustomerAccount())
//                            .tradeType(customerWalletGiveRequest.getWalletRecordTradeType() != null?customerWalletGiveRequest.getWalletRecordTradeType():WalletRecordTradeType.GIVE)
//                            .budgetType(BudgetType.EXPENDITURE)
//                            .dealPrice(balance)
//                            .remark(customerWalletGiveRequest.getRemark())
//                            .dealTime(LocalDateTime.now())
//                            .currentBalance(customerWallet.getBalance().subtract(balance))
//                            .relationOrderId(customerWalletGiveRequest.getRelationOrderId())
//                            .tradeState(TradeStateEnum.PAID).build();
//                    WalletRecord context = walletRecordMerchantService.addWalletRecord(build).getContext();
//                    // 推送金蝶信息 商家减鲸币推送收款退款单
//                    context.setCustomerAccount(companyCodeNew);
//                    pushKingDeeTZS(KsBeanUtil.convert(context,WalletRecordVO.class));
//
//                    Assert.notNull(context,"商户账户增加流水记录异常！");
//                    if (context != null ) {
//                        int i1 = customerWalletRepository.addAmountUser(customerWalletByCustomerId.getCustomerId(), balance);
//                        if (i1 > 0) {
//                            // 增加用户鲸币数量
//                            String recordNo1 = generatorService.generate("G");
//                            AddWalletRecordRecordRequest build1 = AddWalletRecordRecordRequest.builder().recordNo(recordNo1)
//                                    .tradeRemark(customerWalletGiveRequest.getTradeRemark())
//                                    .customerAccount(customerWalletByCustomerId.getCustomerAccount())
//                                    .tradeType(customerWalletGiveRequest.getWalletRecordTradeType() != null ?customerWalletGiveRequest.getWalletRecordTradeType():WalletRecordTradeType.RECHARGE)
//                                    .budgetType(BudgetType.INCOME)
//                                    .dealPrice(balance)
//                                    .remark(customerWalletGiveRequest.getRemark())
//                                    .dealTime(LocalDateTime.now())
//                                    .currentBalance(customerWalletByCustomerId.getBalance().add(balance))
//                                    .relationOrderId(customerWalletGiveRequest.getRelationOrderId())
//                                    .tradeState(TradeStateEnum.PAID).build();
//                            WalletRecord context1 = walletRecordMerchantService.addWalletRecord(build1).getContext();
//                            Assert.notNull(context1, "添加记录失败！");
//                            if (null != context1) {
//                                WalletRecordVO walletRecordVO = new WalletRecordVO();
//                                KsBeanUtil.copyProperties(context1,walletRecordVO);
//                                // 推送金蝶--用户增加鲸币推送收款单
//                                pushKingDeeZS(walletRecordVO);
//                                return BaseResponse.success(walletRecordVO);
//                            }
//                        }
//                    }
//                } else{
//                    throw new SbcRuntimeException("K-111114","商户增加鲸币异常");
//                }
//            } else {
//                // 收回鲸币
//                int i = customerWalletRepository.addAmountOne(customerWallet.getStoreId(), balance);
//                if (i > 0) {
//                    // 记录流水
//                    // 添加商户增加记录
//                    String recordNo = generatorService.generate("G");
//                    AddWalletRecordRecordRequest build = AddWalletRecordRecordRequest.builder().recordNo(recordNo)
//                            .tradeRemark(customerWalletGiveRequest.getTradeRemark())
//                            .customerAccount(customerWallet.getCustomerAccount())
//                            .tradeType(customerWalletGiveRequest.getWalletRecordTradeType())
//                            .budgetType(BudgetType.INCOME)
//                            .dealPrice(balance)
//                            .remark(customerWalletGiveRequest.getRemark())
//                            .dealTime(LocalDateTime.now())
//                            .currentBalance(customerWallet.getBalance().add(balance))
//                            .relationOrderId(customerWalletGiveRequest.getRelationOrderId())
//                            .tradeState(TradeStateEnum.PAID).build();
//                    WalletRecord context = walletRecordMerchantService.addWalletRecord(build).getContext();
//                    Assert.notNull(context,"商户账户增加流水记录异常！");
//                    // 商家加鲸币推送收款单
//                    context.setCustomerAccount(companyCodeNew);
//                    pushKingDeeZS(KsBeanUtil.convert(context, WalletRecordVO.class));
//                    int i1 = customerWalletRepository.balancePay(customerWalletByCustomerId.getCustomerId(), balance);
//                    if (i1 > 0) {
//                        // 减少用户鲸币数量
//                        String recordNo1 = generatorService.generate("G");
//                        AddWalletRecordRecordRequest build1 = AddWalletRecordRecordRequest.builder().recordNo(recordNo1)
//                                .tradeRemark(customerWalletGiveRequest.getTradeRemark())
//                                .customerAccount(customerWalletByCustomerId.getCustomerAccount())
//                                .tradeType(customerWalletGiveRequest.getWalletRecordTradeType())
//                                .budgetType(BudgetType.EXPENDITURE)
//                                .dealPrice(balance)
//                                .remark(customerWalletGiveRequest.getRemark())
//                                .dealTime(LocalDateTime.now())
//                                .relationOrderId(customerWalletGiveRequest.getRelationOrderId())
//                                .currentBalance(customerWalletByCustomerId.getBalance().subtract(balance))
//                                .tradeState(TradeStateEnum.PAID).build();
//                        WalletRecord context1 = walletRecordMerchantService.addWalletRecord(build1).getContext();
//                        if (context1 != null) {
//                            WalletRecordVO walletRecordVO = new WalletRecordVO();
//                            KsBeanUtil.copyProperties(context1,walletRecordVO);
//                            // 用户减少鲸币--推送收款退款单
//                            pushKingDeeTZS(walletRecordVO);
//                            return BaseResponse.success(walletRecordVO);
//                        }
//                    }
//                }
//            }
//
//        } catch (Exception e) {
//            throw new SbcRuntimeException(e);
//        }finally {
//            fairLock.unlock();
//        }
//        return null;
//    }

    private void pushKingDeeZS(WalletRecordVO walletRecordVO,String tid,String prefix) {
        try {
            log.info("调用ERP pushKingDeeZS接口参数======{}",walletRecordVO.toString());
//            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateUtil.FMT_TIME_3, Locale.UK);
//            String sendNo = prefix + LocalDateTime.now().format(dateTimeFormatter) + RandomStringUtils.randomNumeric(4);
            String sendNo = generatorService.generate(prefix);
            // 推送金蝶
            CoinActivityPushKingdeeRequest kingdeeRequest = CoinActivityPushKingdeeRequest
                    .builder()
                    .tid(tid)
                    .buyerAccount(walletRecordVO.getCustomerAccount())
                    .applyPrice(walletRecordVO.getDealPrice())
                    .saleType(SaleType.RETAIL)
                    .sendNo(sendNo)
                    .build();
            walletRecordVO.setSendNo(sendNo);
            tradeProvider.pushOrderKingdeeForCoin(kingdeeRequest);
            log.info("调用ERP pushKingDeeZS接口成功======");
        }catch (Exception e) {
            log.info("pushKingDeeTZS接口异常===={},{}",ExceptionUtils.getMessage(e),e);
            throw new SbcRuntimeException("K-888887","pushKingDeeTZS接口异常");
        }

    }

    private void pushKingDeeTZS(WalletRecordVO walletRecordVO,String tid,String prefix) {
        try {
            log.info("调用ERP pushKingDeeTZS接口参数======{}",walletRecordVO.toString());
//            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateUtil.FMT_TIME_3, Locale.UK);
//            String sendNo = prefix + LocalDateTime.now().format(dateTimeFormatter) + RandomStringUtils.randomNumeric(4);
            String sendNo = generatorService.generate(prefix);
            // 推送金蝶
            CoinActivityPushKingdeeRequest kingdeeRequest = CoinActivityPushKingdeeRequest
                    .builder()
                    .tid(tid)
                    .buyerAccount(walletRecordVO.getCustomerAccount())
                    .applyPrice(walletRecordVO.getDealPrice())
                    .saleType(SaleType.RETAIL)
                    .sendNo(sendNo)
                    .build();
            walletRecordVO.setSendNo(sendNo);
            tradeProvider.pushRefundOrderKingdeeForCoin(kingdeeRequest);
            log.info("调用ERP pushKingDeeTZS接口成功======");
        } catch (Exception e) {
            log.info("pushKingDeeTZS接口异常===={},{}",ExceptionUtils.getMessage(e),e);
            throw new SbcRuntimeException("K-888887","pushKingDeeTZS接口异常");
        }
    }

    /**
     * 查询商户钱包
     * */
    public BaseResponse<CustomerWallet> queryWallet(Long storeId) {
        CustomerWallet customerWallet = customerWalletRepository.findOne(CustomerWalletQueryBuilder.queryByStoreId(String.valueOf(storeId))).orElse(null);
        if (null == customerWallet) {
            throw new SbcRuntimeException("K-888887","该商家暂未开启鲸币权限，请开启后再进行充值！");
        }
        return BaseResponse.success(customerWallet);
    }

    /**
     * 发起提现
     * */
    @LcnTransaction
    @Transactional(noRollbackFor = Exception.class)
    public BaseResponse withdrawal(InitiateWithdrawalRequest request) {
        if (StringUtils.isEmpty(request.getStoreId())) {
            throw new SbcRuntimeException("用户未登录，请先登录");
        }
        // 商户钱包
        CustomerWallet customerWallet = customerWalletRepository.findOne(CustomerWalletQueryBuilder.queryByStoreId(request.getStoreId())).orElse(null);
        StoreByIdResponse context = storeQueryProvider.getById(StoreByIdRequest.builder().storeId(Long.valueOf(request.getStoreId())).build()).getContext();
        StoreVO storeVO = context.getStoreVO();
//        if (context.getStoreVO().getStorePayPassword() == null) {
//            throw new SbcRuntimeException(CustomerErrorCode.NO_CUSTOMER_PAY_PASSWORD, "未设置会员支付密码");
//        }

//        if (storeVO.getPayErrorTime() != null && storeVO.getPayErrorTime() == 3) {
//            Duration duration = Duration.between(storeVO.getPayLockTime(), LocalDateTime.now());
//            if (duration.toMinutes() < 30) {
//                //支付密码输错三次，并且锁定时间还未超过30分钟，返回账户冻结错误信息
////                throw new SbcRuntimeException(CustomerErrorCode.CUSTOMER_PAY_LOCK_TIME_ERROR, new Object[]{30 - duration.toMinutes()});
//                throw new SbcRuntimeException("K-888887", "连续输错密码3次，请" + (30 - duration.toMinutes()) + "分钟后重试！");
//            }
//        }

        // 检查密码是否正确
//        StoreCheckPayPasswordRequest storeCheckPayPasswordRequest = new StoreCheckPayPasswordRequest();
//        storeCheckPayPasswordRequest.setPayPassword(request.getPayPassword());
//        storeCheckPayPasswordRequest.setStoreId(storeVO.getStoreId());
//        storeQueryProvider.checkStorePayPwd(storeCheckPayPasswordRequest);

        // 提现到默认银行卡不需要校验银行卡正确性
        this.checkWithdrawal(request,customerWallet);
        // 商户账号
        String storeId = customerWallet.getCustomerAccount();
        // 银行卡号
        String bankCode = request.getBankCode();
        // 提现金额
        BigDecimal dealPrice = request.getDealPrice();
        //提现时间
        LocalDateTime dateTime = LocalDateTime.now();
        // 添加提现记录
        String w = generatorService.generate("W");
        AddWalletRecordRecordRequest recordRecordRequest = new AddWalletRecordRecordRequest();
        recordRecordRequest.setCustomerId(storeVO.getStoreId().toString());
        recordRecordRequest.setBudgetType(BudgetType.EXPENDITURE);
        recordRecordRequest.setCurrentBalance(customerWallet.getBalance().subtract(dealPrice));
        recordRecordRequest.setCustomerAccount(storeId);
        recordRecordRequest.setStoreId(customerWallet.getStoreId());
        recordRecordRequest.setDealPrice(dealPrice);
        recordRecordRequest.setDealTime(dateTime);
        recordRecordRequest.setExtractType("银行卡");
        recordRecordRequest.setTradeType(WalletRecordTradeType.WITHDRAWAL);
        recordRecordRequest.setTradeState(TradeStateEnum.NOT_PAID);
        recordRecordRequest.setTradeRemark(WalletDetailsType.WITHDRAWAL_REVIEW.getDesc()+"-"+w);
        recordRecordRequest.setRemark(WalletDetailsType.WITHDRAWAL_REVIEW.getDesc());
        recordRecordRequest.setBalance(customerWallet.getBalance().subtract(dealPrice));
        WalletRecord convert = KsBeanUtil.convert(recordRecordRequest, WalletRecord.class);
        convert.setRecordNo(w);
        convert.setRelationOrderId(generatorService.generate("S"));
        WalletRecord walletRecord = walletRecordRepository.saveAndFlush(convert);
        String recordNo = walletRecord.getRecordNo();

        //========================== 第二步 提交提现工单 =================================================
        com.wanmi.sbc.account.api.request.wallet.TicketsFormModifyRequest modifyRequest = new com.wanmi.sbc.account.api.request.wallet.TicketsFormModifyRequest();
        modifyRequest.setRecordNo(recordNo);
        modifyRequest.setWalletId(customerWallet.getWalletId());
//        modifyRequest.setBankName(bankName);
//        modifyRequest.setBankBranch(bankBranch);
        modifyRequest.setBankCode(bankCode);
        modifyRequest.setApplyType(2);
        modifyRequest.setApplyPrice(dealPrice);
        modifyRequest.setApplyTime(dateTime);
        modifyRequest.setExtractStatus(1);
        modifyRequest.setRemark("商户:" + storeVO.getStoreName() + "提现 " + dealPrice + "元。");
        modifyRequest.setCustomerName(request.getCustomerName());
        modifyRequest.setCustomerPhone(request.getCustomerPhone());
        modifyRequest.setAutoType(0);
        modifyRequest.setBankName(request.getBackName());
        modifyRequest.setBankBranch(request.getBankBranch());
        TicketsForm ticketsForm = ticketsFormRepository.save(KsBeanUtil.convert(modifyRequest, TicketsForm.class));
        if (ticketsForm != null && ticketsForm.getFormId() != null) {
            modifyRequest.setFormId(ticketsForm.getFormId());
        }

//        customerWallet.setBalance(customerWallet.getBalance().subtract(dealPrice));
//        customerWallet.setBlockBalance(customerWallet.getBlockBalance().add(dealPrice));
//        customerWalletRepository.saveAndFlush(customerWallet);
        customerWalletRepository.withdrawal(customerWallet.getStoreId(),dealPrice);
        return BaseResponse.success(modifyRequest);
    }

    public void updateUserWallet(CustomerWallet customerWallet) {
        customerWalletRepository.updateIsEnableByStoreId(customerWallet.getIsEnable(), customerWallet.getStoreId());
    }

    public WalletCountResponse queryWalletMoney (WalletByWalletIdAddRequest request) {
        WalletCountResponse walletCountResponse = new WalletCountResponse();
        BigDecimal toltalBalance = new BigDecimal("0.0");
        BigDecimal addCount = new BigDecimal("0.0");
        BigDecimal yeReCount = new BigDecimal("0.0");
        if (request.getMerchantFlag()) {
            Object addObject = customerWalletQueryRepository.storeAddCount();
            if (null != addObject) {
                addCount = new BigDecimal(addObject.toString());
            }
            Object ReObject = customerWalletQueryRepository.storeReduceCount();
            if (null != ReObject) {
                yeReCount = new BigDecimal(ReObject.toString());
            }
            Object balanceCount = customerWalletQueryRepository.storeBalanceCount();
            if(null != balanceCount) {
                toltalBalance =  new BigDecimal(balanceCount.toString());
            }
        } else {
            Object addObject = customerWalletQueryRepository.yeAddCount();
            if (null != addObject) {
                addCount = new BigDecimal(addObject.toString());
            }
            Object yeReduceCount = customerWalletQueryRepository.yeReduceCount();
            if (null != yeReduceCount) {
                yeReCount = new BigDecimal(yeReduceCount.toString());
            }
            Object balanceCount = customerWalletQueryRepository.userBalanceCount();
            if (null != balanceCount) {
                toltalBalance =  new BigDecimal(balanceCount.toString());
            }
        }
        walletCountResponse.setTotalBalance(toltalBalance);
        walletCountResponse.setAddBalance(addCount);
        walletCountResponse.setReduceBalance(yeReCount);
        return walletCountResponse;
    }

    public WalletCountResponse queryWalletCountMoney () {
        WalletCountResponse walletCountResponse = new WalletCountResponse();
        BigDecimal toltalBalance = new BigDecimal("0.0");
        BigDecimal addCount = new BigDecimal("0.0");
        BigDecimal yeReCount = new BigDecimal("0.0");
        Object addObject = customerWalletQueryRepository.taltolAddCount();
        if (null != addObject) {
            addCount = new BigDecimal(addObject.toString());
        }
        Object ReObject = customerWalletQueryRepository.toltalReduceCount();
        if (null != ReObject) {
            yeReCount = new BigDecimal(ReObject.toString());
        }
        Object balanceCount = customerWalletQueryRepository.taltolBalanceCount();
        if(null != balanceCount) {
            toltalBalance =  new BigDecimal(balanceCount.toString());
        }
        walletCountResponse.setTotalBalance(toltalBalance);
        walletCountResponse.setAddBalance(addCount);
        walletCountResponse.setReduceBalance(yeReCount);
        return walletCountResponse;
    }

    public Page<WalletListResponse> userList(WalletUserPageQueryRequest walletUserPageQueryRequest){

        Page<Object> objects = new MicroServicePage<>();
        if (walletUserPageQueryRequest.getStoreFlag()) {
            if (walletUserPageQueryRequest.getIsMoney() != null) {
                List<String> haveJBAccount = customerWalletQueryRepository.findHaveJBStoreAccount(walletUserPageQueryRequest.getIsMoney());
                objects = customerWalletQueryRepository.storeIsNotNull(walletUserPageQueryRequest.getCustomerAccount()
                        ,walletUserPageQueryRequest.getAccountName(),haveJBAccount,walletUserPageQueryRequest.getPageable());
            } else {
                objects = customerWalletQueryRepository.userStore(walletUserPageQueryRequest.getCustomerAccount()
                        ,walletUserPageQueryRequest.getAccountName(),walletUserPageQueryRequest.getPageable());
            }
        } else {
            if (walletUserPageQueryRequest.getIsMoney() != null) {
                // 查询当前有/无鲸币账户
                List<String> haveJBAccount = customerWalletQueryRepository.findHaveJBAccount(walletUserPageQueryRequest.getIsMoney());
                if (!CollectionUtils.isEmpty(haveJBAccount)) {
                    objects = customerWalletQueryRepository.userIsNull(walletUserPageQueryRequest.getCustomerAccount()
                            ,walletUserPageQueryRequest.getAccountName(),haveJBAccount,walletUserPageQueryRequest.getPageable());
                }
            } else {
                objects = customerWalletQueryRepository.userList(walletUserPageQueryRequest.getCustomerAccount()
                        ,walletUserPageQueryRequest.getAccountName(),walletUserPageQueryRequest.getPageable());
            }

        }
        List<WalletListResponse> walletListResponses = convertUserList(objects,walletUserPageQueryRequest.getStoreFlag());
        Page<WalletListResponse> page = new MicroServicePage<>(walletListResponses, walletUserPageQueryRequest.getPageRequest(), objects.getTotalElements());
        return page;
    }

    public CustomerWallet addUserCustomer(String customerId,String customerAccount){
        //用户钱包信息为空，给用户新增一条钱包信息
        CustomerWallet customerWallet = new CustomerWallet();
        customerWallet.setCustomerId(customerId);
        customerWallet.setCustomerAccount(customerAccount);
        customerWallet.setCustomerName(customerAccount);
        customerWallet.setBalance(BigDecimal.ZERO);
        customerWallet.setRechargeBalance(BigDecimal.ZERO);
        customerWallet.setGiveBalance(BigDecimal.ZERO);
        customerWallet.setBlockBalance(BigDecimal.ZERO);
        customerWallet.setCustomerStatus(DefaultFlag.NO);
        customerWallet.setCreateTime(LocalDateTime.now());
        customerWallet.setCreateId(customerId);
        customerWallet.setUpdateTime(LocalDateTime.now());
        customerWallet.setUpdateId(customerId);
        customerWallet.setWithDrawalBalance(BigDecimal.ZERO);
        customerWallet.setDelFlag(DefaultFlag.NO);
        customerWallet.setGiveBalanceState(DefaultFlag.NO.toValue());
        return customerWallet;
    }

    public CustomerWallet addStoreCustomer(String storeId) {
        //用户钱包信息为空，给用户新增一条钱包信息
        StoreByIdResponse storeInfo = storeQueryProvider.getById(StoreByIdRequest.builder().storeId(Long.valueOf(storeId)).build()).getContext();
        EmployeeListRequest employeeListRequest = new EmployeeListRequest();
        employeeListRequest.setCompanyInfoId(storeInfo.getStoreVO().getCompanyInfo().getCompanyInfoId());
        employeeListRequest.setIsMasterAccount(1);
        List<EmployeeListVO> employeeList = employeeQueryProvider.list(employeeListRequest).getContext().getEmployeeList();
        if (employeeListRequest == null) {
            return null;
        }
        EmployeeListVO employeeListVO = employeeList.get(0);
        CustomerWallet customerWallet = new CustomerWallet();
        customerWallet.setCustomerId(storeId);
        customerWallet.setCustomerAccount(employeeListVO.getEmployeeName());
        customerWallet.setCustomerName(employeeListVO.getEmployeeName());
        customerWallet.setBalance(BigDecimal.ZERO);
        customerWallet.setRechargeBalance(BigDecimal.ZERO);
        customerWallet.setGiveBalance(BigDecimal.ZERO);
        customerWallet.setBlockBalance(BigDecimal.ZERO);
        customerWallet.setCustomerStatus(DefaultFlag.NO);
        customerWallet.setCreateTime(LocalDateTime.now());
        customerWallet.setCreateId(storeId);
        customerWallet.setUpdateTime(LocalDateTime.now());
        customerWallet.setUpdateId(storeId);
        customerWallet.setWithDrawalBalance(BigDecimal.ZERO);
        customerWallet.setDelFlag(DefaultFlag.NO);
        customerWallet.setGiveBalanceState(DefaultFlag.NO.toValue());
        return customerWallet;
    }

    private List<WalletListResponse> convertUserList(Page<Object> page,Boolean storeFlag) {
        List<WalletListResponse> list = new ArrayList<>();
        CustomerWalletSupplierRequest request = new CustomerWalletSupplierRequest();
        List<CustomerWallet> all =new ArrayList<>();
//        if (storeFlag) {
//            request.setStoredIds(page.getContent().stream().map(item -> {
//                Object[] cast = StringUtil.cast(item, Object[].class);
//                BigInteger cast1 = new BigInteger(cast[2].toString());
//                return cast1.longValue();
//            }).collect(Collectors.toList()));
//            all = customerWalletRepository.findAll(CustomerWalletSupplierPageBuilder.build(request));
//        } else {
//            request.setCustomerIds( page.getContent().stream().map(item -> {
//                Object[] cast = StringUtil.cast(item, Object[].class);
//                return StringUtil.cast(cast, 2, String.class);
//            }).collect(Collectors.toList()));
//            all = customerWalletRepository.findAll(CustomerWalletSupplierPageBuilder.build(request));
//        }

        List<CustomerWallet> finalAll = all;
        page.getContent().stream().forEach(item->{
            Object[] cast = StringUtil.cast(item, Object[].class);
            WalletListResponse walletListResponse = new WalletListResponse();
            String contractName= StringUtil.cast(cast, 0, String.class);
            String contractPhone= StringUtil.cast(cast, 1, String.class);
            BigInteger walletId = StringUtil.cast(cast,3,BigInteger.class);
            BigDecimal balance = StringUtil.cast(cast, 4, BigDecimal.class);
//            String customerId;
            walletListResponse.setCustomerAccount(contractName);
            walletListResponse.setAccount(contractPhone);
            walletListResponse.setWalletId(walletId.longValue());
            walletListResponse.setBalance(balance != null ? balance : new BigDecimal("0.00"));
//            if (storeFlag) {
//                BigInteger cast1 = new BigInteger(cast[2].toString());
//                customerId = cast1.toString();
//                finalAll.stream().forEach(wallet->{
//                    if (wallet.getStoreId().equals(customerId)) {
//                        walletListResponse.setWalletId(wallet.getWalletId());
//                        walletListResponse.setBalance(wallet.getBalance());
//                    }
//                });
//            } else {
//                customerId = StringUtil.cast(cast, 2, String.class);;
//                finalAll.stream().forEach(wallet->{
//                    if (wallet.getCustomerId().equals(customerId)) {
//                        walletListResponse.setWalletId(wallet.getWalletId());
//                        walletListResponse.setBalance(wallet.getBalance());
//                    }
//                });
//            }

            String account = walletListResponse.getAccount();
            // 查询当前用户最近一次获得的时间和鲸币和最近一次扣除的时间和鲸币
            // 查询最近一次获得的时间和鲸币
            Object record = customerWalletQueryRepository.walletRecord(0, account);
            if (record != null) {
                Object []cast1 = StringUtil.cast(record, Object[].class);
                walletListResponse.setJTime((Timestamp)cast1[0]);
                walletListResponse.setJMoney(new BigDecimal(cast1[1].toString()));
            }
            // 查询最后一次获得的时间和鲸币
            Object record1 = customerWalletQueryRepository.walletRecord(1, account);
            if (record1 != null) {
                Object []cast2 = StringUtil.cast(record1, Object[].class);
                walletListResponse.setHTime((Timestamp)cast2[0]);
                walletListResponse.setHMoney(new BigDecimal(cast2[1].toString()));
            }
            list.add(walletListResponse);
        });
        return list;
    }

    public WalletInfoResponse userByWalletId(Long walletId,Boolean storeFlag){
        WalletInfoResponse response = new WalletInfoResponse();
        Object o = null;
        if (storeFlag) {
            o =  customerWalletQueryRepository.storeInfo(walletId);
        } else {
            o = customerWalletQueryRepository.customerInfo(walletId);
        }
        if(o == null) {
            throw new SbcRuntimeException("K-999999","未找到相应用户，请重新选择");
        }
        Object[] cast = StringUtil.cast(o, Object[].class);
        response.setAccount(StringUtil.cast(cast,0,String.class));
        response.setLogo(StringUtil.cast(cast,1,String.class));
        response.setName(StringUtil.cast(cast,2,String.class));
        response.setBalance(new BigDecimal(cast[3].toString()));
        BigInteger cast1 = new BigInteger(cast[4].toString());
        response.setWalletId(cast1.longValue());
        return response;
    }

    private void checkWithdrawal(InitiateWithdrawalRequest request, CustomerWallet customerWallet) {
        //提现金额
        BigDecimal dealPrice = request.getDealPrice();
        //余额
        BigDecimal balance = customerWallet.getBalance();
        //充值金额
        BigDecimal rechargeBalance = customerWallet.getRechargeBalance();
        //赠送金额
        BigDecimal giveBalance = customerWallet.getGiveBalance();
        //提现金额不得大于余额,提现金额不得大于充值金额,若有赠送金额,提现金额不得大于充值金额减去赠送金额
        BigDecimal bigDecimal = new BigDecimal(0);
        //提现金额为0
        if (dealPrice.compareTo(bigDecimal) == 0) {
            throw new SbcRuntimeException("k-210005", "提现金额不能为0");
        }
        if (bigDecimal.compareTo(dealPrice) == 1) {
            throw new SbcRuntimeException("k-210005", "提现金额不能是负数");
        }
        if (balance.compareTo(bigDecimal) == 0) {
            throw new SbcRuntimeException("k-210005", "余额为0");
        }
        //提现金额大于余额
        if (dealPrice.compareTo(balance) == 1) {
            throw new SbcRuntimeException("k-210001", "提现金额大于鲸贴");
        }
        if (dealPrice.compareTo(BigDecimal.valueOf(1)) < 0) {
            throw new SbcRuntimeException("k-210006", "提现金额最低1元");
        }
    }


}
