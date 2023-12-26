package com.wanmi.sbc.trade;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.account.api.provider.funds.CustomerFundsDetailProvider;
import com.wanmi.sbc.account.api.provider.funds.CustomerFundsProvider;
import com.wanmi.sbc.account.api.provider.funds.CustomerFundsQueryProvider;
import com.wanmi.sbc.account.api.provider.wallet.CustomerWalletProvider;
import com.wanmi.sbc.account.api.provider.wallet.CustomerWalletQueryProvider;
import com.wanmi.sbc.account.api.provider.wallet.VirtualGoodsQueryProvider;
import com.wanmi.sbc.account.api.provider.wallet.WalletRecordProvider;
import com.wanmi.sbc.account.api.request.wallet.*;
import com.wanmi.sbc.account.api.response.wallet.BalanceByCustomerIdResponse;
import com.wanmi.sbc.account.bean.enums.BudgetType;
import com.wanmi.sbc.account.bean.enums.TradeStateEnum;
import com.wanmi.sbc.account.bean.enums.WalletDetailsType;
import com.wanmi.sbc.account.bean.enums.WalletRecordTradeType;
import com.wanmi.sbc.account.bean.vo.VirtualGoodsVO;
import com.wanmi.sbc.account.bean.vo.WalletRecordVO;
import com.wanmi.sbc.common.annotation.MultiSubmit;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.*;
import com.wanmi.sbc.customer.api.constant.CustomerErrorCode;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.loginregister.CustomerSiteProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerGetByIdRequest;
import com.wanmi.sbc.customer.api.request.loginregister.CustomerCheckPayPasswordRequest;
import com.wanmi.sbc.customer.api.response.customer.CustomerGetByIdResponse;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoProvider;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoLockStockRequest;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoLockStockDTO;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.marketing.bean.constant.Constant;
import com.wanmi.sbc.mq.ProducerService;
import com.wanmi.sbc.order.api.provider.payorder.PayOrderProvider;
import com.wanmi.sbc.order.api.provider.trade.*;
import com.wanmi.sbc.order.api.provider.trade.newPileTrade.NewPileTradeProvider;
import com.wanmi.sbc.order.api.request.pickuprecord.PickUpRecordAddRequest;
import com.wanmi.sbc.order.api.request.trade.*;
import com.wanmi.sbc.order.api.response.trade.TradeCheckResponse;
import com.wanmi.sbc.order.api.response.trade.TradeGetByIdResponse;
import com.wanmi.sbc.order.api.response.trade.TradeListByParentIdResponse;
import com.wanmi.sbc.order.bean.dto.*;
import com.wanmi.sbc.order.bean.enums.FlowState;
import com.wanmi.sbc.order.bean.enums.PayState;
import com.wanmi.sbc.order.bean.enums.TradeActivityTypeEnum;
import com.wanmi.sbc.order.bean.vo.*;
import com.wanmi.sbc.pay.api.provider.*;
import com.wanmi.sbc.pay.api.request.*;
import com.wanmi.sbc.pay.api.response.*;
import com.wanmi.sbc.pay.bean.enums.*;
import com.wanmi.sbc.pay.bean.vo.PayChannelItemVO;
import com.wanmi.sbc.pay.bean.vo.PayGatewayConfigVO;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.setting.api.provider.AuditQueryProvider;
import com.wanmi.sbc.setting.api.provider.WechatAuthProvider;
import com.wanmi.sbc.setting.api.provider.storewechatminiprogramconfig.StoreWechatMiniProgramConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.storewechatminiprogramconfig.StoreWechatMiniProgramConfigByCacheRequest;
import com.wanmi.sbc.setting.api.response.MiniProgramSetGetResponse;
import com.wanmi.sbc.setting.api.response.storewechatminiprogramconfig.StoreWechatMiniProgramConfigByCacheResponse;
import com.wanmi.sbc.setting.bean.vo.ConfigVO;
import com.wanmi.sbc.third.login.api.WechatApi;
import com.wanmi.sbc.third.login.response.GetAccessTokeResponse;
import com.wanmi.sbc.third.login.response.LittleProgramAuthResponse;
import com.wanmi.sbc.trade.request.CcbPayRequest;
import com.wanmi.sbc.trade.request.PayMobileRequest;
import com.wanmi.sbc.trade.request.WeiXinPayRequest;
import com.wanmi.sbc.util.CommonUtil;
import io.jsonwebtoken.lang.Assert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;


/**
 * 支付
 * Created by sunkun on 2017/8/10.
 */
@RestController
@RequestMapping("/pay")
@Validated
@Api(tags = "PayController", description = "mobile 支付")
@Slf4j
public class PayController {

    @Autowired
    private TradeProvider tradeProvider;

    @Autowired
    private NewPileTradeProvider newPileTradeProvider;

    @Autowired
    private PileTradeProvider pileTradeProvider;

    @Autowired
    private PileTradeQueryProvider pileTradeQueryProvider;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private PayProvider payProvider;

    @Autowired
    private PayServiceHelper payServiceHelper;

    @Autowired
    private PayQueryProvider payQueryProvider;

    @Autowired
    private WxPayProvider wxPayProvider;

    @Autowired
    private AliPayProvider aliPayProvider;

    @Autowired
    private WechatApi wechatApi;

    @Autowired
    private WechatAuthProvider wechatAuthProvider;

    @Autowired
    private CustomerFundsProvider customerFundsProvider;

    @Autowired
    private CustomerFundsQueryProvider customerFundsQueryProvider;

    @Autowired
    private CustomerWalletQueryProvider walletQueryProvider;

    @Autowired
    private CustomerWalletProvider customerWalletProvider;

    @Autowired
    private CustomerFundsDetailProvider customerFundsDetailProvider;

    @Autowired
    private CustomerSiteProvider customerSiteProvider;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private StoreWechatMiniProgramConfigQueryProvider storeWechatMiniProgramConfigQueryProvider;

    @Autowired
    private ProviderTradeQueryProvider providerTradeQueryProvider;

    @Autowired
    private ProviderTradeProvider providerTradeProvider;

    @Autowired
    private RedisService redisService;

    @Autowired
    private WalletRecordProvider walletRecordProvider;

    @Autowired
    private VirtualGoodsQueryProvider virtualGoodsQueryProvider;

    @Autowired
    private CustomerWalletQueryProvider customerWalletQueryProvider;

    @Autowired
    private AuditQueryProvider auditQueryProvider;

    @Autowired
    private CmbPayProvider cmbPayProvider;

    @Autowired
    private CupsPayProvider cupsPayProvider;

    @Autowired
    private PayOrderProvider payOrderProvider;

    @Autowired
    private GoodsInfoProvider goodsInfoProvider;

    @Autowired
    ProducerService producerService;


    /**
     * 锁的默认时间为3秒
     */
    private static final long REPEAT_LOCK_TIME = 3L;

    /**
     * 创建Charges
     *
     * @param payMobileRequest
     * @return
     */
    @ApiOperation(value = "创建Charges", notes = "返回的支付对象实际为支付凭证，由前端获取后通过JS请求第三方支付")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @MultiSubmit
    @LcnTransaction
    public BaseResponse<Object> create(@RequestBody @Valid PayMobileRequest payMobileRequest) {
        List<TradeVO> trades = payServiceHelper.findTrades(payMobileRequest.getTid());
        payServiceHelper.checkPayBefore(trades);
        TradeVO trade = trades.get(0);
        PayOrderVO payOrder =
                tradeQueryProvider.getPayOrderById(TradeGetPayOrderByIdRequest.builder().payOrderId(trade.getPayOrderId()).build()).getContext().getPayOrder();
        PayExtraRequest payExtraRequest = new PayExtraRequest();
        if (payMobileRequest.getSuccessUrl() != null) {
            payExtraRequest.setSuccessUrl(payMobileRequest.getSuccessUrl());
        }
        payExtraRequest.setStoreId(commonUtil.getStoreIdWithDefault());
        payExtraRequest.setBusinessId(payMobileRequest.getTid());
        payExtraRequest.setChannelItemId(payMobileRequest.getChannelItemId());
        payExtraRequest.setTerminal(payMobileRequest.getTerminal());
        payExtraRequest.setAmount(trade.getTradePrice().getTotalPrice());
        //TODO 订单标题及订单描述待添加
        payExtraRequest.setOpenId(payMobileRequest.getOpenId());
        String title = trade.getTradeItems().get(0).getSkuName();
        String body = trade.getTradeItems().get(0).getSkuName() + " " + (trade.getTradeItems().get(0).getSpecDetails
                () == null ? "" : trade.getTradeItems().get(0).getSpecDetails());
        if (trade.getTradeItems().size() > 1) {
            if (title.length() > 23) {
//                title = String.format("s%s% %s", title.substring(0, 22), "...", "  等多件商品");
                title = title.substring(0, 22) + "...  等多件商品";
            } else {
                title = title + " 等多件商品";
            }
            body = body + " 等多件商品";
        } else {
            if (title.length() > 29) {
                title = title.substring(0, 28) + "...";
            }
        }

        payExtraRequest.setSubject(title);
        payExtraRequest.setBody(body);

        payExtraRequest.setClientIp(HttpUtil.getIpAddr());
        Object object;
        try {
            object = payProvider.getPayCharge(payExtraRequest).getContext().getObject();
        } catch (SbcRuntimeException e) {
            if (e.getErrorCode() != null && e.getErrorCode().equals("K-100203")) {
                Operator operator = Operator.builder().ip(HttpUtil.getIpAddr()).adminId("1").name("system")
                        .account("system").platform
                                (Platform.BOSS).build();
                tradeProvider.payCallBackOnline(TradePayCallBackOnlineRequest.builder()
                        .trade(KsBeanUtil.convert(trade, TradeDTO.class))
                        .payOrderOld(KsBeanUtil.convert(payOrder, PayOrderDTO.class))
                        .operator(operator)
                        .build());
            }
            throw new SbcRuntimeException(e.getErrorCode(), e.getParams());
        }
        return BaseResponse.success(object);
    }


    /**
     * 获取支付网关配置
     *
     * @return
     */
    @ApiOperation(value = "获取支付网关配置")
    @RequestMapping(value = "/getWxConfig", method = RequestMethod.GET)
    public BaseResponse<String> getWxConfig() {
        GatewayOpenedByStoreIdRequest request = new GatewayOpenedByStoreIdRequest();
        request.setStoreId(commonUtil.getStoreIdWithDefault());
        List<PayGatewayConfigVO> payGatewayConfigs = payQueryProvider.listOpenedGatewayConfig(request).getContext()
                .getGatewayConfigVOList();
        Optional<PayGatewayConfigVO> optional = payGatewayConfigs.stream().filter(
                c ->
                        c.getPayGateway().getName() == PayGatewayEnum.WECHAT
        ).findFirst();
        return BaseResponse.success(optional.get().getAppId2());
    }

    /**
     * 获取可用支付项
     *
     * @return
     */
    @ApiOperation(value = "获取可用支付项")
    @RequestMapping(value = "/items/{type}", method = RequestMethod.GET)
    public BaseResponse<List<PayChannelItemVO>> items(@PathVariable String type) {
        GatewayOpenedByStoreIdRequest request = new GatewayOpenedByStoreIdRequest();
        request.setStoreId(commonUtil.getStoreIdWithDefault());
        List<PayGatewayConfigVO> payGatewayConfigList = payQueryProvider.listOpenedGatewayConfig(request).getContext()
                .getGatewayConfigVOList();
        List<PayChannelItemVO> itemList = new ArrayList<>();
        payGatewayConfigList.forEach(config -> {
            List<PayChannelItemVO> payChannelItemList = payQueryProvider.listOpenedChannelItemByGatewayName(new
                    OpenedChannelItemRequest(
                    config.getPayGateway().getName(), TerminalType.valueOf(type))).getContext().getPayChannelItemVOList();
            if (CollectionUtils.isNotEmpty(payChannelItemList)) {
                itemList.addAll(payChannelItemList);
            }
        });
        return BaseResponse.success(itemList);
    }

    /**
     * 获取微信openid
     *
     * @param code
     * @return
     */
    @ApiOperation(value = "获取微信openid")
    @RequestMapping(value = "/getWxOpenId/{code}", method = RequestMethod.GET)
    public BaseResponse<String> getWxOpenId(@PathVariable String code) {
        WxCodeRequest wxCodeRequest = new WxCodeRequest();
        wxCodeRequest.setCode(code);
        wxCodeRequest.setStoreId(commonUtil.getStoreIdWithDefault());
        return BaseResponse.success(payQueryProvider.getWxOpenIdByCodeAndStoreId(wxCodeRequest).getContext()
                .getOpenId());
    }

    /**
     * 非微信浏览器h5支付统一下单接口
     *
     * @param weiXinPayRequest
     * @return
     */
    @ApiOperation(value = "非微信浏览器h5支付统一下单接口", notes = "返回结果mweb_url为拉起微信支付收银台的中间页面，可通过访问该url来拉起微信客户端，完成支付")
    @RequestMapping(value = "/wxPayUnifiedorderForMweb", method = RequestMethod.POST)
    public BaseResponse<WxPayForMWebResponse> wxPayUnifiedorderForMweb(@RequestBody @Valid WeiXinPayRequest weiXinPayRequest) {
        WxPayForMWebRequest mWebRequest = new WxPayForMWebRequest();
        String id = payServiceHelper.getPayBusinessId(weiXinPayRequest.getTid(), weiXinPayRequest.getParentTid());
        List<TradeVO> trades = payServiceHelper.checkTrades(id);
        //订单总金额
        String totalPrice = payServiceHelper.calcTotalPriceByPenny(trades).toString();
        String body = payServiceHelper.buildBody(trades);
        mWebRequest.setBody(body + "订单");
        mWebRequest.setOut_trade_no(id);
        mWebRequest.setTotal_fee(totalPrice);
        mWebRequest.setSpbill_create_ip(HttpUtil.getIpAddr());
        mWebRequest.setTrade_type(WxPayTradeType.MWEB.toString());
        mWebRequest.setScene_info("{\"h5_info\": {\"type\":\"Wap\",\"wap_url\": \"https://m.s2btest2.kstore.shop\"," +
                "\"wap_name\": \"h5下单支付\"}}");
        mWebRequest.setStoreId(commonUtil.getStoreIdWithDefault());
        return wxPayProvider.wxPayForMWeb(mWebRequest);
    }

    /**
     * 非微信浏览器h5支付统一下单接口
     *
     * @param weiXinPayRequest
     * @return
     */
    @ApiOperation(value = "非微信浏览器h5支付统一下单接口", notes = "返回结果mweb_url为拉起微信支付收银台的中间页面，可通过访问该url来拉起微信客户端，完成支付")
    @RequestMapping(value = "/pile/wxPayUnifiedorderForMweb", method = RequestMethod.POST)
    public BaseResponse<WxPayForMWebResponse> pileWxPayUnifiedorderForMweb(@RequestBody @Valid WeiXinPayRequest weiXinPayRequest) {
        WxPayForMWebRequest mWebRequest = new WxPayForMWebRequest();
        String id = payServiceHelper.getPayBusinessId(weiXinPayRequest.getTid(), weiXinPayRequest.getParentTid());
        List<TradeVO> trades = payServiceHelper.checkPileTrades(id);
        //订单总金额
        String totalPrice = payServiceHelper.calcTotalPriceByPenny(trades).toString();
        String body = payServiceHelper.buildBody(trades);
        mWebRequest.setBody(body + "订单");
        mWebRequest.setOut_trade_no(id);
        mWebRequest.setTotal_fee(totalPrice);
        mWebRequest.setSpbill_create_ip(HttpUtil.getIpAddr());
        mWebRequest.setTrade_type(WxPayTradeType.MWEB.toString());
        mWebRequest.setScene_info("{\"h5_info\": {\"type\":\"Wap\",\"wap_url\": \"https://m.s2btest2.kstore.shop\"," +
                "\"wap_name\": \"h5下单支付\"}}");
        mWebRequest.setStoreId(commonUtil.getStoreIdWithDefault());
        return wxPayProvider.wxPilePayForMWeb(mWebRequest);
    }


    /**
     * 微信浏览器内JSApi支付统一下单接口
     *
     * @param weiXinPayRequest
     * @return
     */
    @ApiOperation(value = "微信浏览器内JSApi支付统一下单接口", notes = "返回用于在微信内支付的所需参数")
    @RequestMapping(value = "/wxPayUnifiedorderForJSApi", method = RequestMethod.POST)
    @MultiSubmit
    public BaseResponse<Map<String, String>> wxPayUnifiedorderForJSApi(@RequestBody @Valid WeiXinPayRequest weiXinPayRequest) {
        return wxPayProvider.wxPayForJSApi(wxPayCommon(weiXinPayRequest));
    }


    /**
     * 微信浏览器内JSApi支付统一下单接口
     *
     * @param weiXinPayRequest
     * @return
     */
    @ApiOperation(value = "微信浏览器内JSApi支付统一下单接口", notes = "返回用于在微信内支付的所需参数")
    @RequestMapping(value = "/pile/wxPayUnifiedorderForJSApi", method = RequestMethod.POST)
    @MultiSubmit
    public BaseResponse<Map<String, String>> pileWxPayUnifiedorderForJSApi(@RequestBody @Valid WeiXinPayRequest weiXinPayRequest) {
        return wxPayProvider.pileWxPayForJSApi(wxPayCommon(weiXinPayRequest));
    }


    /**
     * 微信浏览器内JSApi支付统一下单接口  ——  好友代付
     *
     * @param weiXinPayRequest
     * @return
     */
    @ApiOperation(value = "微信浏览器内JSApi支付统一下单接口", notes = "返回用于在微信内支付的所需参数")
    @RequestMapping(value = "/wxFriendPayUnifiedorderForJSApi", method = RequestMethod.POST)
    @MultiSubmit
    public BaseResponse<Map<String, String>> wxFriendPayUnifiedorderForJSApi(@RequestBody @Valid WeiXinPayRequest weiXinPayRequest) {
        return BaseResponse.error("APP版本太低，请更新您的APP再进行支付，谢谢！");
        /*if (needLockStock(weiXinPayRequest)) {
            //检查订单条目：1 促销活动 2 买商品赠券 3 使用的优惠券已过期
            TradeCheckResponse tradeCheckResponse = tradeQueryProvider.checkTrade(TradeCheckRequest.builder()
                            .parentTid(weiXinPayRequest.getParentTid())
                            .tid(weiXinPayRequest.getTid())
                            .build())
                    .getContext();

            if (tradeCheckResponse.getType() != 0) {
                return BaseResponse.error("很遗憾，您的好友所购买的商品存在库存不足或活动已失效，请稍后再试");
            }
        }

        String payOrderNo = generateNewPayOrderNo(weiXinPayRequest);
        weiXinPayRequest.setChannelId(16L);
        weiXinPayRequest.setPayOrderNo(payOrderNo);

        String id = payServiceHelper.getPayBusinessId(weiXinPayRequest.getTid(), weiXinPayRequest.getParentTid());
        List<TradeVO> tradeVOs = payServiceHelper.checkTrades(id);
        for (TradeVO vo : tradeVOs) {
            vo.setPayOrderNo(payOrderNo);
            tradeProvider.update(TradeUpdateRequest.builder().trade(KsBeanUtil.convert(vo, TradeUpdateDTO.class)).build());
        }

        //囤货和囤货提货不进行支付锁库存
        if (!needLockStock(weiXinPayRequest)) {
            return wxPayProvider.wxPayForJSApi(wxPayCommon(weiXinPayRequest));
        }

        //TODO: 2023-6-2 重构
//        1.获取订单分布式锁
//        2. 写入占库存订单表：订单号（唯一索引），确定支付时间，
//        成功进行第2步，失败不再处理
//        3. 锁定本地库存，失败返回
//                第三方付款
//        4. 建立5分钟后取消支付延时任务
        List<String> tids = tradeVOs.stream().map(TradeVO::getId).sorted().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(tids)) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "未找到对应的订单号，请检查！");
        }

        List<RLock> rLocks = new ArrayList<>();
        tids.forEach(tid -> {
            RLock rLock = redissonClient.getFairLock(tid);
            rLock.lock();
            rLocks.add(rLock);
        });

        try {
            BaseResponse<Map<String, Integer>> lockStockResp = lockStock(tradeVOs);
            if (!CommonErrorCode.SUCCESSFUL.equals(lockStockResp.getCode())) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "库存锁定失败，请稍后尝试！");
            }
            BaseResponse<Map<String, String>> response = wxPayProvider.wxPayForJSApi(wxPayCommon(weiXinPayRequest));

            // 创建取消支付延时任务
            Map<String, Integer> lockResultMap = lockStockResp.getContext();
            if (MapUtils.isNotEmpty(lockResultMap) && lockResultMap.values().stream().allMatch(lockResult -> lockResult == 1)) {
                producerService.createCancelPayDelayTask(tids);
            }
            return response;
        } finally {
            //解锁
            rLocks.forEach(Lock::unlock);
        }*/
    }

    private static boolean needLockStock(WeiXinPayRequest weiXinPayRequest) {
        return !(StringUtils.isNotEmpty(weiXinPayRequest.getTid()) && (
                weiXinPayRequest.getTid().startsWith(GeneratorService._NEW_PILE_PREFIX_TRADE_ID)
                        || weiXinPayRequest.getTid().startsWith(GeneratorService._NEW_PILE_PREFIX_TRADE_PK_ID)
        )
        );
    }

    private String generateNewPayOrderNo(WeiXinPayRequest weiXinPayRequest) {
        String payOrderNo;
        String id = payServiceHelper.getPayBusinessId(weiXinPayRequest.getTid(), weiXinPayRequest.getParentTid());
        if (StringUtils.isNotEmpty(weiXinPayRequest.getTid()) && weiXinPayRequest.getTid().startsWith(GeneratorService._NEW_PILE_PREFIX_TRADE_ID)) {
            payOrderNo = payOrderProvider.generateNewPayOrderByOrderCodeNewPile(StringRequest.builder().value(id)
                    .storeId(Constants.BOSS_DEFAULT_STORE_ID)
                    .channelId(weiXinPayRequest.getChannelId()).
                    build()).getContext();
        } else {
            payOrderNo = payOrderProvider.generateNewPayOrderByOrderCode(StringRequest.builder().value(id)
                    .storeId(Constants.BOSS_DEFAULT_STORE_ID)
                    .channelId(weiXinPayRequest.getChannelId()).
                    build()).getContext();
        }
        return payOrderNo;
    }

    /**
     * 微信浏览器内JSApi支付统一下单接口  ——  好友代付
     *
     * @param weiXinPayRequest
     * @return
     */
    @ApiOperation(value = "微信浏览器内JSApi支付统一下单接口", notes = "返回用于在微信内支付的所需参数")
    @RequestMapping(value = "/pile/wxFriendPayUnifiedorderForJSApi", method = RequestMethod.POST)
    @MultiSubmit
    public BaseResponse<Map<String, String>> pileWxFriendPayUnifiedorderForJSApi(@RequestBody @Valid WeiXinPayRequest weiXinPayRequest) {
        weiXinPayRequest.setChannelId(16L);
        weiXinPayRequest.setPayOrderNo(generateNewPayOrderNo(weiXinPayRequest));
        return wxPayProvider.pileWxPayForJSApi(pileWxPayCommon(weiXinPayRequest));
    }


    /**
     * 非微信浏览器h5支付统一下单接口
     *
     * @param weiXinPayRequest
     * @return
     */
    @ApiOperation(value = "好友代付：非微信浏览器h5支付统一下单接口", notes = "返回结果mweb_url为拉起微信支付收银台的中间页面，可通过访问该url来拉起微信客户端，完成支付")
    @RequestMapping(value = "/replaceWxPayUnifiedorderForMweb", method = RequestMethod.POST)
    public BaseResponse<WxPayForMWebResponse> replaceWxPayUnifiedorderForMweb(@RequestBody @Valid WeiXinPayRequest weiXinPayRequest) {
        try {
            if (redisService.setNx(weiXinPayRequest.getTid())) {
                redisService.expireBySeconds(weiXinPayRequest.getTid(), REPEAT_LOCK_TIME);
                WxPayForMWebRequest mWebRequest = new WxPayForMWebRequest();
                String id = payServiceHelper.getPayBusinessId(weiXinPayRequest.getTid(), weiXinPayRequest.getParentTid());
                List<TradeVO> trades = payServiceHelper.checkTrades(id);
                //订单总金额
                String totalPrice = payServiceHelper.calcTotalPriceByPenny(trades).toString();
                String body = payServiceHelper.buildBody(trades);
                mWebRequest.setBody(body + "订单");
                mWebRequest.setOut_trade_no(id);
                mWebRequest.setTotal_fee(totalPrice);
                mWebRequest.setSpbill_create_ip(HttpUtil.getIpAddr());
                mWebRequest.setTrade_type(WxPayTradeType.MWEB.toString());
                mWebRequest.setScene_info("{\"h5_info\": {\"type\":\"Wap\",\"wap_url\": \"https://m.s2btest2.kstore.shop\"," +
                        "\"wap_name\": \"h5下单支付\"}}");
                mWebRequest.setStoreId(commonUtil.getStoreIdWithDefault());
                return wxPayProvider.wxPayForMWeb(mWebRequest);
            }
            throw new SbcRuntimeException("K-100206");
        } catch (SbcRuntimeException e) {
            return BaseResponse.info(e.getErrorCode(), e.getMessage());
        }
    }


    /**
     * 微信浏览器内JSApi支付统一下单接口
     *
     * @param weiXinPayRequest
     * @return
     */
    @ApiOperation(value = "好友代付：微信浏览器内JSApi支付统一下单接口", notes = "返回用于在微信内支付的所需参数")
    @RequestMapping(value = "/replaceWxPayUnifiedorderForJSApi", method = RequestMethod.POST)
    public BaseResponse<Map<String, String>> replaceWxPayUnifiedorderForJSApi(@RequestBody @Valid WeiXinPayRequest weiXinPayRequest) {
        try {
            if (redisService.setNx(weiXinPayRequest.getTid())) {
                redisService.expireBySeconds(weiXinPayRequest.getTid(), REPEAT_LOCK_TIME);
                return wxPayProvider.wxPayForJSApi(wxPayCommon(weiXinPayRequest));
            }
            throw new SbcRuntimeException("K-100206");
        } catch (SbcRuntimeException e) {
            return BaseResponse.info(e.getErrorCode(), e.getMessage());
        }
    }

    /**
     * 小程序内JSApi支付统一下单接口
     *
     * @param weiXinPayRequest
     * @return
     */
    @ApiOperation(value = "小程序内JSApi支付统一提货下单接口", notes = "返回用于在小程序内支付的所需参数")
    @RequestMapping(value = "/wxPayTakeGoodUnifiedorderForLittleProgram", method = RequestMethod.POST)
    @MultiSubmit
    public BaseResponse<Map<String, String>> wxPayTakeGoodUnifiedorderForLittleProgram(@RequestBody @Valid WeiXinPayRequest weiXinPayRequest) {
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        String appId;
        if (Objects.nonNull(domainInfo)) {
            BaseResponse<StoreWechatMiniProgramConfigByCacheResponse> cacheResponse = storeWechatMiniProgramConfigQueryProvider.getCacheByStoreId(StoreWechatMiniProgramConfigByCacheRequest
                    .builder()
                    .storeId(domainInfo.getStoreId())
                    .build());
            if (Objects.nonNull(cacheResponse)
                    && Objects.nonNull(cacheResponse.getContext())
                    && Objects.nonNull(cacheResponse.getContext().getStoreWechatMiniProgramConfigVO())) {
                appId = cacheResponse.getContext().getStoreWechatMiniProgramConfigVO().getAppId();
            } else {
                throw new SbcRuntimeException(CommonErrorCode.METHOD_NOT_ALLOWED);
            }
        } else {
            BaseResponse<MiniProgramSetGetResponse> baseResponse = wechatAuthProvider.getMiniProgramSet();
            if (baseResponse.getCode().equals(CommonErrorCode.SUCCESSFUL)) {
                JSONObject json = JSON.parseObject(baseResponse.getContext().getContext());
                appId = json.getString("appId");
            } else {
                throw new SbcRuntimeException(CommonErrorCode.FAILED);
            }
        }
        WxPayForJSApiRequest req = wxPayCommon(weiXinPayRequest);
        req.setAppid(appId);
        return wxPayProvider.wxPayTakeGoodForLittleProgram(req);
    }

    /**
     * 小程序内JSApi囤货支付统一下单接口
     *
     * @param weiXinPayRequest
     * @return
     */
    @ApiOperation(value = "小程序内JSApi囤货支付统一下单接口", notes = "返回用于在小程序内支付的所需参数")
    @RequestMapping(value = "/pile/wxPayUnifiedorderForLittleProgram", method = RequestMethod.POST)
    @MultiSubmit
    public BaseResponse<Map<String, String>> pileWxPayUnifiedorderForLittleProgram(@RequestBody @Valid WeiXinPayRequest weiXinPayRequest) {
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        String appId;
        if (Objects.nonNull(domainInfo)) {
            BaseResponse<StoreWechatMiniProgramConfigByCacheResponse> cacheResponse = storeWechatMiniProgramConfigQueryProvider.getCacheByStoreId(StoreWechatMiniProgramConfigByCacheRequest
                    .builder()
                    .storeId(domainInfo.getStoreId())
                    .build());
            if (Objects.nonNull(cacheResponse)
                    && Objects.nonNull(cacheResponse.getContext())
                    && Objects.nonNull(cacheResponse.getContext().getStoreWechatMiniProgramConfigVO())) {
                appId = cacheResponse.getContext().getStoreWechatMiniProgramConfigVO().getAppId();
            } else {
                throw new SbcRuntimeException(CommonErrorCode.METHOD_NOT_ALLOWED);
            }
        } else {
            BaseResponse<MiniProgramSetGetResponse> baseResponse = wechatAuthProvider.getMiniProgramSet();
            if (baseResponse.getCode().equals(CommonErrorCode.SUCCESSFUL)) {
                JSONObject json = JSON.parseObject(baseResponse.getContext().getContext());
                appId = json.getString("appId");
            } else {
                throw new SbcRuntimeException(CommonErrorCode.FAILED);
            }
        }

        //组合支付判断金额
        if (Objects.nonNull(weiXinPayRequest.getOnlineTotalFee()) && BigDecimal.ZERO.compareTo(weiXinPayRequest.getOnlineTotalFee()) > -1) {
            throw new SbcRuntimeException("K-050520");
        }

        WxPayForJSApiRequest req = pileWxPayCommon(weiXinPayRequest);
        req.setAppid(appId);
        return wxPayProvider.pileWxPayForLittleProgram(req);
    }

    /**
     * 小程序内JSApi支付统一下单接口
     *
     * @param weiXinPayRequest
     * @return
     */
    @ApiOperation(value = "小程序内JSApi支付统一下单接口", notes = "返回用于在小程序内支付的所需参数")
    @RequestMapping(value = "/wxPayUnifiedorderForLittleProgram", method = RequestMethod.POST)
    @MultiSubmit
    public BaseResponse<Map<String, String>> wxPayUnifiedorderForLittleProgram(@RequestBody @Valid WeiXinPayRequest weiXinPayRequest) {
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        String appId;
        if (Objects.nonNull(domainInfo)) {
            BaseResponse<StoreWechatMiniProgramConfigByCacheResponse> cacheResponse = storeWechatMiniProgramConfigQueryProvider.getCacheByStoreId(StoreWechatMiniProgramConfigByCacheRequest
                    .builder()
                    .storeId(domainInfo.getStoreId())
                    .build());
            if (Objects.nonNull(cacheResponse)
                    && Objects.nonNull(cacheResponse.getContext())
                    && Objects.nonNull(cacheResponse.getContext().getStoreWechatMiniProgramConfigVO())) {
                appId = cacheResponse.getContext().getStoreWechatMiniProgramConfigVO().getAppId();
            } else {
                throw new SbcRuntimeException(CommonErrorCode.METHOD_NOT_ALLOWED);
            }
        } else {
            BaseResponse<MiniProgramSetGetResponse> baseResponse = wechatAuthProvider.getMiniProgramSet();
            if (baseResponse.getCode().equals(CommonErrorCode.SUCCESSFUL)) {
                JSONObject json = JSON.parseObject(baseResponse.getContext().getContext());
                appId = json.getString("appId");
            } else {
                throw new SbcRuntimeException(CommonErrorCode.FAILED);
            }
        }
        WxPayForJSApiRequest req = wxPayCommon(weiXinPayRequest);
        req.setAppid(appId);
        return wxPayProvider.wxPayForLittleProgram(req);
    }


    /**
     * 小程序内JSApi支付统一充值接口
     *
     * @param weiXinPayRequest
     * @return
     */
    @ApiOperation(value = "小程序内JSApi支付统一充值接口", notes = "返回用于在小程序内充值支付的所需参数")
    @RequestMapping(value = "/wxPayRechargeUnifiedorderForLittleProgram", method = RequestMethod.POST)
    @MultiSubmit
    public BaseResponse<Map<String, String>> wxPayRechargeUnifiedorderForLittleProgram(@RequestBody @Valid WeiXinPayRequest weiXinPayRequest) {
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        String appId;
        if (Objects.nonNull(domainInfo)) {
            BaseResponse<StoreWechatMiniProgramConfigByCacheResponse> cacheResponse = storeWechatMiniProgramConfigQueryProvider.getCacheByStoreId(StoreWechatMiniProgramConfigByCacheRequest
                    .builder()
                    .storeId(domainInfo.getStoreId())
                    .build());
            if (Objects.nonNull(cacheResponse)
                    && Objects.nonNull(cacheResponse.getContext())
                    && Objects.nonNull(cacheResponse.getContext().getStoreWechatMiniProgramConfigVO())) {
                appId = cacheResponse.getContext().getStoreWechatMiniProgramConfigVO().getAppId();
            } else {
                throw new SbcRuntimeException(CommonErrorCode.METHOD_NOT_ALLOWED);
            }
        } else {
            BaseResponse<MiniProgramSetGetResponse> baseResponse = wechatAuthProvider.getMiniProgramSet();
            if (baseResponse.getCode().equals(CommonErrorCode.SUCCESSFUL)) {
                JSONObject json = JSON.parseObject(baseResponse.getContext().getContext());
                appId = json.getString("appId");
            } else {
                throw new SbcRuntimeException(CommonErrorCode.FAILED);
            }
        }
        WxPayForJSApiRequest req = wxPayRechargeCommon(weiXinPayRequest);
        req.setAppid(appId);
        return wxPayProvider.wxPayRechargeForLittleProgram(req);
    }

    /**
     * 微信内浏览器,小程序支付公用逻辑
     *
     * @param weiXinPayRequest
     * @return
     */
    private WxPayForJSApiRequest pileWxPayCommon(WeiXinPayRequest weiXinPayRequest) {
        WxPayForJSApiRequest jsApiRequest = new WxPayForJSApiRequest();
        String id = payServiceHelper.getPayBusinessId(weiXinPayRequest.getTid(), weiXinPayRequest.getParentTid());
        List<TradeVO> trades = payServiceHelper.checkPileTrades(id);
        BigDecimal totalPriceByPenny = payServiceHelper.calcTotalPriceByPenny(trades);
        String totalPrice = totalPriceByPenny.toString();

        //计算订单金额是否正确
        //查询用户账户
        TradeVO tradeVO = trades.stream().findFirst().orElse(null);
        BalanceByCustomerIdResponse context = customerWalletQueryProvider.getBalanceByCustomerId(WalletByCustomerIdQueryRequest.builder().customerId(tradeVO.getBuyer().getId()).build())
                .getContext();
//        if(Objects.isNull(context) || Objects.isNull(context.getCustomerWalletVO())){
//            throw new SbcRuntimeException("K-010005");
//        }
        //前端传入金额（标识为组合支付）
        if (Objects.nonNull(weiXinPayRequest.getOnlineTotalFee())) {
            BigDecimal totalFee = context.getCustomerWalletVO().getBalance().add(weiXinPayRequest.getOnlineTotalFee());
            if (totalPriceByPenny.compareTo(totalFee) != 0) {
                throw new SbcRuntimeException("K-050520");
            }

            //支付金额取（订单金额-余额金额）
            BigDecimal subtract = totalPriceByPenny.subtract(context.getCustomerWalletVO().getBalance());
            if (BigDecimal.ZERO.compareTo(subtract) > -1) {
                throw new SbcRuntimeException("K-050520");
            }
            totalPrice = subtract.toString();
        }

        String body = payServiceHelper.buildBody(trades);
        jsApiRequest.setBody(body + "订单");
        jsApiRequest.setOut_trade_no(id);
        jsApiRequest.setTotal_fee(totalPrice);
        jsApiRequest.setSpbill_create_ip(HttpUtil.getIpAddr());
        jsApiRequest.setTrade_type(WxPayTradeType.JSAPI.toString());
        jsApiRequest.setOpenid(weiXinPayRequest.getOpenid());
        jsApiRequest.setStoreId(commonUtil.getStoreIdWithDefault());
        return jsApiRequest;
    }

    /**
     * 微信内浏览器,小程序支付公用逻辑
     *
     * @param weiXinPayRequest
     * @return
     */
    private WxPayForJSApiRequest wxPayCommon(WeiXinPayRequest weiXinPayRequest) {
        Assert.hasText(weiXinPayRequest.getPayOrderNo(), "payOrderNo不能为空");

        WxPayForJSApiRequest jsApiRequest = new WxPayForJSApiRequest();
        String id = payServiceHelper.getPayBusinessId(weiXinPayRequest.getTid(), weiXinPayRequest.getParentTid());
        List<TradeVO> trades = payServiceHelper.checkTrades(id);
        String totalPrice = payServiceHelper.calcTotalPriceByPenny(trades).toString();
        String body = payServiceHelper.buildBody(trades);
        jsApiRequest.setBody(body + "订单");
        jsApiRequest.setOut_trade_no(id);
        jsApiRequest.setTotal_fee(totalPrice);
        jsApiRequest.setSpbill_create_ip(HttpUtil.getIpAddr());
        jsApiRequest.setTrade_type(WxPayTradeType.JSAPI.toString());
        jsApiRequest.setOpenid(weiXinPayRequest.getOpenid());
        jsApiRequest.setStoreId(commonUtil.getStoreIdWithDefault());
        jsApiRequest.setPayOrderNo(weiXinPayRequest.getPayOrderNo());
        return jsApiRequest;
    }

    private WxPayForJSApiRequest wxPayRechargeCommon(WeiXinPayRequest weiXinPayRequest) {
        WxPayForJSApiRequest jsApiRequest = new WxPayForJSApiRequest();
        String id = weiXinPayRequest.getTid();
        QueryWalletRecordRequest queryWalletRecordRequest = new QueryWalletRecordRequest();
        queryWalletRecordRequest.setRecordNo(id);
        //查询充值交易记录表
        List<WalletRecordVO> walletRecordVOs = walletRecordProvider.queryWalletRecord(queryWalletRecordRequest).getContext().getWalletRecordVOs();
        WalletRecordVO walletRecordVO = walletRecordVOs.get(0);
        //商品表
        List<Long> ids = new ArrayList<>();
        ids.add(Long.valueOf(walletRecordVO.getVirtualGoodsId()));
        VirtualGoodsRequest request = new VirtualGoodsRequest();
        request.setGoodsIdList(ids);
        VirtualGoodsVO virtualGoodsVO = virtualGoodsQueryProvider.getVirtualGoodsList(request).getContext().getVirtualGoodsList().get(0);
        String totalPrice = walletRecordVO.getDealPrice().toString();
        String body = virtualGoodsVO.getGoodsName();
        jsApiRequest.setBody(body + "订单");
        jsApiRequest.setOut_trade_no(id);
        jsApiRequest.setTotal_fee(totalPrice);
        jsApiRequest.setSpbill_create_ip(HttpUtil.getIpAddr());
        jsApiRequest.setTrade_type(WxPayTradeType.JSAPI.toString());
        jsApiRequest.setOpenid(weiXinPayRequest.getOpenid());
        jsApiRequest.setStoreId(commonUtil.getStoreIdWithDefault());
        return jsApiRequest;
    }


    /**
     * 微信app支付统一下单接口
     *
     * @param weiXinPayRequest
     * @return
     */
    @ApiOperation(value = " 微信app支付统一下单接口", notes = "返回用于app内支付的所需参数")
    @RequestMapping(value = "/wxPayUnifiedorderForApp", method = RequestMethod.POST)
    @MultiSubmit
    public BaseResponse<Map<String, String>> wxPayUnifiedorderForApp(@RequestBody @Valid WeiXinPayRequest weiXinPayRequest) {
        WxPayForAppRequest appRequest = new WxPayForAppRequest();
        String id = payServiceHelper.getPayBusinessId(weiXinPayRequest.getTid(), weiXinPayRequest.getParentTid());
        List<TradeVO> trades = payServiceHelper.checkTrades(id);
        //订单总金额
        String totalPrice = payServiceHelper.calcTotalPriceByPenny(trades).toString();
        String body = payServiceHelper.buildBody(trades);
        appRequest.setBody(body + "订单");
        appRequest.setOut_trade_no(id);
        appRequest.setTotal_fee(totalPrice);
        appRequest.setSpbill_create_ip(HttpUtil.getIpAddr());
        appRequest.setTrade_type(WxPayTradeType.APP.toString());
        appRequest.setStoreId(Constants.BOSS_DEFAULT_STORE_ID);
        return wxPayProvider.wxPayForApp(appRequest);
    }

    /**
     * 微信app支付统一下单接口
     *
     * @param weiXinPayRequest
     * @return
     */
    @ApiOperation(value = " 囤货微信app支付统一下单接口", notes = "返回用于app内支付的所需参数")
    @RequestMapping(value = "/wxPayUnifiedPileOrderForApp", method = RequestMethod.POST)
    @MultiSubmit
    public BaseResponse<Map<String, String>> wxPayUnifiedPileOrderForApp(@RequestBody @Valid WeiXinPayRequest weiXinPayRequest) {
        WxPayForAppRequest appRequest = new WxPayForAppRequest();
        String id = payServiceHelper.getPayBusinessId(weiXinPayRequest.getTid(), weiXinPayRequest.getParentTid());
        List<TradeVO> trades = payServiceHelper.checkPileTrades(id);
        //订单总金额
        String totalPrice = payServiceHelper.calcTotalPriceByPenny(trades).toString();
        String body = payServiceHelper.buildBody(trades);
        appRequest.setBody(body + "订单");
        appRequest.setOut_trade_no(id);
        appRequest.setTotal_fee(totalPrice);
        appRequest.setSpbill_create_ip(HttpUtil.getIpAddr());
        appRequest.setTrade_type(WxPayTradeType.APP.toString());
        appRequest.setStoreId(Constants.BOSS_DEFAULT_STORE_ID);
        log.info("======================================>囤货微信app支付统计下单开始<=======================================");
        return wxPayProvider.wxPayPileOrderForApp(appRequest);
    }

    /**
     * 微信app提货支付统一下单接口
     *
     * @param weiXinPayRequest
     * @return
     */
    @ApiOperation(value = " 微信app提货支付统一下单接口", notes = "返回用于app内支付的所需参数")
    @RequestMapping(value = "/wxTakeGoodPayUnifiedorderForApp", method = RequestMethod.POST)
    @MultiSubmit
    public BaseResponse<Map<String, String>> wxTakeGoodPayUnifiedorderForApp(@RequestBody @Valid WeiXinPayRequest weiXinPayRequest) {
        WxPayForAppRequest appRequest = new WxPayForAppRequest();
        String id = payServiceHelper.getPayBusinessId(weiXinPayRequest.getTid(), weiXinPayRequest.getParentTid());
        List<TradeVO> trades = payServiceHelper.checkTrades(id);
        //订单总金额
        String totalPrice = payServiceHelper.calcTotalPriceByPenny(trades).toString();
        String body = payServiceHelper.buildBody(trades);
        appRequest.setBody(body + "订单");
        appRequest.setOut_trade_no(id);
        appRequest.setTotal_fee(totalPrice);
        appRequest.setSpbill_create_ip(HttpUtil.getIpAddr());
        appRequest.setTrade_type(WxPayTradeType.APP.toString());
        appRequest.setStoreId(Constants.BOSS_DEFAULT_STORE_ID);
        return wxPayProvider.wxPayTakeGoodForApp(appRequest);
    }

    /**
     * 微信app充值支付统一下单接口
     *
     * @param weiXinPayRequest
     * @return
     */
    @ApiOperation(value = " 微信app充值支付统一下单接口", notes = "返回用于app内支付的所需参数")
    @RequestMapping(value = "/wxPayRechargeUnifiedorderForApp", method = RequestMethod.POST)
    @MultiSubmit
    public BaseResponse<Map<String, String>> wxPayRechargeUnifiedorderForApp(@RequestBody @Valid WeiXinPayRequest weiXinPayRequest) {
        WxPayForAppRequest appRequest = new WxPayForAppRequest();
        String id = weiXinPayRequest.getTid();
        //获取充值记录信息
        WalletRecordVO walletRecordVO = walletRecordProvider.getWalletRecordByRecordNo(WalletRecordRequest.builder().recordNo(id).build()).getContext().getWalletRecordVO();
        //获取虚拟商品信息
        VirtualGoodsVO virtualGoodsVO = virtualGoodsQueryProvider.getVirtualGoods(VirtualGoodsRequest.builder().goodsId(Long.valueOf(walletRecordVO.getVirtualGoodsId())).build()).getContext().getVirtualGoods();
        //订单总金额
        String totalPrice = walletRecordVO.getDealPrice().multiply(new BigDecimal(100)).stripTrailingZeros().toPlainString();
        //设置订单体
        String body = virtualGoodsVO.getGoodsName();
        appRequest.setBody(body + "订单");
        //设置订单id
        appRequest.setOut_trade_no(id);
        //设置订单价格
        appRequest.setTotal_fee(totalPrice);
        //设置请求ip
        appRequest.setSpbill_create_ip(HttpUtil.getIpAddr());
        //设置请求类型
        appRequest.setTrade_type(WxPayTradeType.APP.toString());
        //设置店铺id
        appRequest.setStoreId(Constants.BOSS_DEFAULT_STORE_ID);
        return wxPayProvider.wxPayRechargeForApp(appRequest);
    }

    @ApiOperation(value = "充值支付校验", notes = "充值前校验是否已支付成功", httpMethod = "GET")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单号", required = true)
    @RequestMapping(value = "/aliPayRecharge/check/{tid}", method = RequestMethod.GET)
    public BaseResponse checkPayRechargeState(@PathVariable String tid) {
        WalletRecordVO walletRecordVO = walletRecordProvider.getWalletRecordByRecordNo(WalletRecordRequest.builder().recordNo(tid).build()).getContext().getWalletRecordVO();
        return BaseResponse.success(walletRecordVO.getTradeState());
    }

    @ApiOperation(value = "支付校验", notes = "支付前校验是否已支付成功", httpMethod = "GET")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单号", required = true)
    @RequestMapping(value = "/aliPay/check/{tid}", method = RequestMethod.GET)
    public BaseResponse checkPayState(@PathVariable String tid) {
        TradeVO trade = tradeQueryProvider.getById(TradeGetByIdRequest.builder()
                .tid(tid).build()).getContext().getTradeVO();
        //0:未支付
        String flag = "0";
        if (Objects.nonNull(trade)) {
            if (Objects.nonNull(trade.getTradeState())) {
                LocalDateTime orderTimeOut = trade.getOrderTimeOut();
                //已支付
                if (PayState.PAID.equals(trade.getTradeState().getPayState())) {
                    flag = "1";
                }
                //已超过未支付取消订单时间或者已作废
                if (FlowState.VOID.equals(trade.getTradeState().getFlowState()) || Objects.nonNull(orderTimeOut) && orderTimeOut.isBefore(LocalDateTime.now())) {
                    flag = "2";
                }
            }
        }
        return BaseResponse.success(flag);
    }

    @ApiOperation(value = "屯货支付前校验是否已支付成功")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单号", required = true)
    @RequestMapping(value = "/pile/aliPay/check/{tid}", method = RequestMethod.GET)
    public BaseResponse pileCheckPayState(@PathVariable String tid) {
        NewPileTradeVO trade = newPileTradeProvider.getById(TradeGetByIdRequest.builder()
                .tid(tid).build()).getContext().getTradeVO();

        String flag = "0";
        if (Objects.nonNull(trade)) {
            if (Objects.nonNull(trade.getTradeState())) {
                if (PayState.PAID.equals(trade.getTradeState().getPayState())) {
                    flag = "1";
                }
            }
        }
        return BaseResponse.success(flag);
    }


    @ApiOperation(value = "支付校验", notes = "支付前校验是否已支付成功", httpMethod = "GET")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单号", required = true)
    @RequestMapping(value = "/share/check", method = RequestMethod.GET)
    public BaseResponse shareCheckPayState(@RequestBody TradeGetByIdRequest request) {
        TradeVO trade = tradeQueryProvider.getById(request).getContext().getTradeVO();
        //0:未支付
        String flag = "0";
        if (Objects.nonNull(trade)) {
            if (Objects.nonNull(trade.getTradeState())) {
                LocalDateTime orderTimeOut = trade.getOrderTimeOut();
                //已支付
                if (PayState.PAID.equals(trade.getTradeState().getPayState())) {
                    flag = "1";
                }
                //已超过未支付取消订单时间或者已作废
                if (FlowState.VOID.equals(trade.getTradeState().getFlowState()) || Objects.nonNull(orderTimeOut) && orderTimeOut.isBefore(LocalDateTime.now())) {
                    flag = "2";
                }
            }
        }
        return BaseResponse.success(flag);
    }

    @ApiOperation(value = "微信支付订单支付状态校验", notes = "微信支付后校验是否已支付成功", httpMethod = "GET")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单号", required = true)
    @RequestMapping(value = "/weiXinPay/checkOrderPayState/{tid}", method = RequestMethod.GET)
    public BaseResponse checkOrderPayState(@PathVariable String tid) {
        String flag = "0";
        List<TradeVO> tradeVOList = new ArrayList<>();
        if (tid.startsWith(GeneratorService._PREFIX_TRADE_ID) || tid.startsWith(GeneratorService._PREFIX_RETAIL_TRADE_ID)) {
            tradeVOList.add(tradeQueryProvider.getById(TradeGetByIdRequest.builder()
                    .tid(tid).build()).getContext().getTradeVO());
        } else if (tid.startsWith(GeneratorService._PREFIX_PARENT_TRADE_ID)) {
            tradeVOList.addAll(tradeQueryProvider.getListByParentId(TradeListByParentIdRequest.builder().parentTid(tid)
                    .build()).getContext().getTradeVOList());
        } else {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        if (tradeVOList.size() > 0 && tradeVOList.get(0).getTradeState().getPayState() == PayState.PAID) {
            flag = "1";
        }
        return BaseResponse.success(flag);
    }

    /*
     * @Description: 支付表单
     * @Param:
     * @Author: Bob->
     * @Date: 2019-02-01 11:12
     */
    @ApiOperation(value = "H5支付宝支付表单", notes = "该请求需新打开一个空白页，返回的支付宝脚本会自动提交重定向到支付宝收银台", httpMethod = "GET")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "encrypted", value = "base64编码后的支付请求",
            required = true)
    @RequestMapping(value = "/aliPay/{encrypted}", method = RequestMethod.GET)
    @LcnTransaction
    public void aliPay(@PathVariable String encrypted, HttpServletResponse response) {
        String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
        PayMobileRequest payMobileRequest = JSON.parseObject(decrypted, PayMobileRequest.class);
        log.info("====================支付宝支付表单================payMobileRequest :{}", payMobileRequest);

        payMobileRequest.setStoreId(commonUtil.getStoreIdWithDefault(payMobileRequest.getOrigin()));
        String form = this.alipayUtil(payMobileRequest);

        try {
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write(form);//直接将完整的表单html输出到页面
            response.getWriter().flush();
            response.getWriter().close();
        } catch (IOException e) {
            // TODO: 2019-01-28 gb支付异常未处理
            log.error("execute alipay has IO exception:{} ", e);
        }
    }


    /*
     * @Description: 支付好友代付
     * @Param:
     * @Author: Bob->
     * @Date: 2019-02-01 11:12
     */
    @ApiOperation(value = "好友代付：H5支付宝支付表单", notes = "该请求需新打开一个空白页，返回的支付宝脚本会自动提交重定向到支付宝收银台", httpMethod = "GET")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "encrypted", value = "base64编码后的支付请求",
            required = true)
    @RequestMapping(value = "/replaceAliPay/{encrypted}", method = RequestMethod.GET)
    @LcnTransaction
    public void replaceAliPay(@PathVariable String encrypted, HttpServletResponse response) throws IOException {
        String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
        PayMobileRequest payMobileRequest = JSON.parseObject(decrypted, PayMobileRequest.class);
        String form = "";
        try {
            if (redisService.setNx(payMobileRequest.getTid())) {
                redisService.expireBySeconds(payMobileRequest.getTid(), REPEAT_LOCK_TIME);
                log.info("====================支付宝支付表单================payMobileRequest :{}", payMobileRequest);
                payMobileRequest.setStoreId(commonUtil.getStoreIdWithDefault(payMobileRequest.getOrigin()));
                form = this.alipayUtil(payMobileRequest);
                response.setContentType("text/html;charset=UTF-8");
            } else {
                throw new SbcRuntimeException("K-100206");
            }
        } catch (SbcRuntimeException e) {
            form = JSON.toJSONString(new BaseResponse<>(e.getErrorCode()));
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.setContentType("application/json;charset=utf-8");
        } catch (Exception e) {
            // TODO: 2019-01-28 gb支付异常未处理
            log.error("execute alipay has IO exception:{} ", e);
        } finally {
            if (StringUtils.isBlank(form)) {
                form = "发生未知错误，请重试";
            }
            response.getWriter().write(form);
            response.getWriter().flush();
            response.getWriter().close();
        }
    }

    /*
     * @Description: app支付表单
     * @Param:
     * @Author: Bob->
     * @Date: 2019-02-01 11:12
     */
    @ApiOperation(value = "APP支付宝支付", notes = "APP支付宝签名后的参数,返回的数据直接调用appSDK")
    @RequestMapping(value = "app/aliPay/", method = RequestMethod.POST)
    @LcnTransaction
    @MultiSubmit
    public BaseResponse<String> appAliPay(@RequestBody @Valid PayMobileRequest payMobileRequest) {
        log.info("====================支付宝支付表单=APP支付宝支付================payMobileRequest :{}", payMobileRequest);
        payMobileRequest.setStoreId(Constant.BOSS_DEFAULT_STORE_ID);

        return BaseResponse.success(this.alipayUtil(payMobileRequest));
    }

    @ApiOperation(value = "APP囤货支付宝支付", notes = "APP支付宝签名后的参数,返回的数据直接调用appSDK")
    @RequestMapping(value = "app/pile/aliPay/", method = RequestMethod.POST)
    @LcnTransaction
    @MultiSubmit
    public BaseResponse<String> appPileAliPay(@RequestBody @Valid PayMobileRequest payMobileRequest) {
        log.info("====================支付宝支付表单=APP支付宝支付================payMobileRequest :{}", payMobileRequest);
        payMobileRequest.setStoreId(Constant.BOSS_DEFAULT_STORE_ID);

        return BaseResponse.success(this.alipayPileUtil(payMobileRequest));
    }

    @ApiOperation(value = "APP支付宝提货支付", notes = "APP支付宝签名后的参数,返回的数据直接调用appSDK")
    @RequestMapping(value = "app/aliPay/takeGood", method = RequestMethod.POST)
    @LcnTransaction
    @MultiSubmit
    public BaseResponse<String> appAliPayTakeGood(@RequestBody @Valid PayMobileRequest payMobileRequest) {
        log.info("====================支付宝支付表单=APP支付宝支付================payMobileRequest :{}", payMobileRequest);
        payMobileRequest.setStoreId(Constant.BOSS_DEFAULT_STORE_ID);

        return BaseResponse.success(this.alipayTakeGoodUtil(payMobileRequest));
    }

    /*
     * @Description: 支付之前的公共判断条件及数据组装
     * @Param:  payMobileRequest
     * @Author: Bob
     * @Date: 2019-02-22 11:27
     */
    private String alipayTakeGoodUtil(PayMobileRequest payMobileRequest) {
        String id = payServiceHelper.getPayBusinessId(payMobileRequest.getTid(), payMobileRequest.getParentTid());
        List<TradeVO> trades = payServiceHelper.checkTrades(id);


        BigDecimal totalPrice = payServiceHelper.calcTotalPriceByYuan(trades);
        PayExtraRequest payExtraRequest = new PayExtraRequest();
        payExtraRequest.setStoreId(payMobileRequest.getStoreId());
        payExtraRequest.setBusinessId(id);
        payExtraRequest.setChannelItemId(payMobileRequest.getChannelItemId());
        payExtraRequest.setTerminal(payMobileRequest.getTerminal());
        if (TerminalType.H5.equals(payMobileRequest.getTerminal())) {
            payExtraRequest.setSuccessUrl(payMobileRequest.getSuccessUrl());
        }
        payExtraRequest.setAmount(totalPrice);
        payExtraRequest.setOpenId(payMobileRequest.getOpenId());
        TradeVO trade = trades.get(0);
        String title = trade.getTradeItems().get(0).getSkuName();
        String body = trade.getTradeItems().get(0).getSkuName() + " " + (trade.getTradeItems().get(0).getSpecDetails
                () == null ? "" : trade.getTradeItems().get(0).getSpecDetails());
        if (trades.size() > 1 || trade.getTradeItems().size() > 1) {
            if (title.length() > 23) {
                title = title.substring(0, 22) + "...  等多件商品";
            } else {
                title = title + " 等多件商品";
            }
            body = body + " 等多件商品";
        } else {
            if (title.length() > 29) {
                title = title.substring(0, 28) + "...";
            }
        }
        title = title.replaceAll("&", "");
        body = body.replaceAll("&", "");
        log.info("=============body", body);
        log.info("=============title", title);
        payExtraRequest.setSubject(title);
        payExtraRequest.setBody(body);
        payExtraRequest.setClientIp(HttpUtil.getIpAddr());

        String form = "";
        try {
            form = aliPayProvider.getPayTakeGoodForm(payExtraRequest).getContext().getForm();
        } catch (SbcRuntimeException e) {
            if (e.getErrorCode() != null && e.getErrorCode().equals("K-100203")) {
                //已支付，手动回调
                Operator operator = Operator.builder().ip(HttpUtil.getIpAddr()).adminId("1").name("SYSTEM").platform
                        (Platform.BOSS).build();

                List<TradePayCallBackOnlineDTO> list = new ArrayList<>();
                trades.forEach(i -> {
                    //获取订单信息
                    PayOrderVO payOrder = tradeQueryProvider.getPayOrderById(TradeGetPayOrderByIdRequest.builder()
                            .payOrderId(i.getPayOrderId()).build()).getContext().getPayOrder();
                    TradePayCallBackOnlineDTO dto = TradePayCallBackOnlineDTO.builder()
                            .payOrderOld(KsBeanUtil.convert(payOrder, PayOrderDTO.class))
                            .trade(KsBeanUtil.convert(i, TradeDTO.class))
                            .build();
                    list.add(dto);

                });
                tradeProvider.payTakeGoodCallBackOnlineBatch(new TradePayCallBackOnlineBatchRequest(list, operator));
            }
            throw new SbcRuntimeException(e.getErrorCode(), e.getParams());
        }

        return form;
    }

    /**
     * @Description: 支付之前的公共判断条件及数据组装
     * @Param: payMobileRequest
     * @Author: marsjiang
     * @Date: 20211001
     */
    private String alipayPileUtil(PayMobileRequest payMobileRequest) {
        String id = payServiceHelper.getPayBusinessId(payMobileRequest.getTid(), payMobileRequest.getParentTid());
        List<TradeVO> trades = payServiceHelper.checkPileTrades(id);


        BigDecimal totalPrice = payServiceHelper.calcTotalPriceByYuan(trades);
        PayExtraRequest payExtraRequest = new PayExtraRequest();
        payExtraRequest.setStoreId(payMobileRequest.getStoreId());
        payExtraRequest.setBusinessId(id);
        payExtraRequest.setChannelItemId(payMobileRequest.getChannelItemId());
        payExtraRequest.setTerminal(payMobileRequest.getTerminal());
        if (TerminalType.H5.equals(payMobileRequest.getTerminal())) {
            payExtraRequest.setSuccessUrl(payMobileRequest.getSuccessUrl());
        }
        payExtraRequest.setAmount(totalPrice);
        payExtraRequest.setOpenId(payMobileRequest.getOpenId());
        TradeVO trade = trades.get(0);
        String title = trade.getTradeItems().get(0).getSkuName();
        String body = trade.getTradeItems().get(0).getSkuName() + " " + (trade.getTradeItems().get(0).getSpecDetails
                () == null ? "" : trade.getTradeItems().get(0).getSpecDetails());
        if (trades.size() > 1 || trade.getTradeItems().size() > 1) {
            if (title.length() > 23) {
                title = title.substring(0, 22) + "...  等多件商品";
            } else {
                title = title + " 等多件商品";
            }
            body = body + " 等多件商品";
        } else {
            if (title.length() > 29) {
                title = title.substring(0, 28) + "...";
            }
        }
        title = title.replaceAll("&", "");
        body = body.replaceAll("&", "");
        log.info("=============body", body);
        log.info("=============title", title);
        payExtraRequest.setSubject(title);
        payExtraRequest.setBody(body);
        payExtraRequest.setClientIp(HttpUtil.getIpAddr());

        String form = "";
        try {
            form = aliPayProvider.getPilePayForm(payExtraRequest).getContext().getForm();
        } catch (SbcRuntimeException e) {
            if (e.getErrorCode() != null && e.getErrorCode().equals("K-100203")) {
                //已支付，手动回调
                Operator operator = Operator.builder().ip(HttpUtil.getIpAddr()).adminId("1").name("SYSTEM").platform
                        (Platform.BOSS).build();

                List<TradePayCallBackOnlineDTO> list = new ArrayList<>();
                trades.forEach(i -> {
                    //获取订单信息
                    PayOrderVO payOrder = pileTradeQueryProvider.getPayOrderById(TradeGetPayOrderByIdRequest.builder()
                            .payOrderId(i.getPayOrderId()).build()).getContext().getPayOrder();
                    TradePayCallBackOnlineDTO dto = TradePayCallBackOnlineDTO.builder()
                            .payOrderOld(KsBeanUtil.convert(payOrder, PayOrderDTO.class))
                            .trade(KsBeanUtil.convert(i, TradeDTO.class))
                            .build();
                    list.add(dto);

                });
                pileTradeProvider.payCallBackOnlineBatch(new TradePayCallBackOnlineBatchRequest(list, operator));
            }
            throw new SbcRuntimeException(e.getErrorCode(), e.getParams());
        }

        return form;
    }

    /*
     * @Description: 支付之前的公共判断条件及数据组装
     * @Param:  payMobileRequest
     * @Author: Bob
     * @Date: 2019-02-22 11:27
     */
    private String alipayUtil(PayMobileRequest payMobileRequest) {
        String id = payServiceHelper.getPayBusinessId(payMobileRequest.getTid(), payMobileRequest.getParentTid());
        List<TradeVO> trades = payServiceHelper.checkTrades(id);

        BigDecimal totalPrice = payServiceHelper.calcTotalPriceByYuan(trades);

        //查询后台配置支付超时时间
        List<ConfigVO> configVOList = auditQueryProvider.listTradeConfig().getContext().getConfigVOList();
        ConfigVO timeoutCancel = configVOList.stream().filter(c -> ("order_setting_timeout_cancel").equals(c.getConfigType())).findFirst().orElse(null);

        PayExtraRequest payExtraRequest = new PayExtraRequest();
        payExtraRequest.setStoreId(payMobileRequest.getStoreId());
        payExtraRequest.setBusinessId(id);
        payExtraRequest.setChannelItemId(payMobileRequest.getChannelItemId());
        payExtraRequest.setTerminal(payMobileRequest.getTerminal());
        if (TerminalType.H5.equals(payMobileRequest.getTerminal())) {
            payExtraRequest.setSuccessUrl(payMobileRequest.getSuccessUrl());
        }
        payExtraRequest.setAmount(totalPrice);
        payExtraRequest.setOpenId(payMobileRequest.getOpenId());
        TradeVO trade = trades.get(0);
        String title = trade.getTradeItems().get(0).getSkuName();
        String body = trade.getTradeItems().get(0).getSkuName() + " " + (trade.getTradeItems().get(0).getSpecDetails
                () == null ? "" : trade.getTradeItems().get(0).getSpecDetails());
        if (trades.size() > 1 || trade.getTradeItems().size() > 1) {
            if (title.length() > 23) {
                title = title.substring(0, 22) + "...  等多件商品";
            } else {
                title = title + " 等多件商品";
            }
            body = body + " 等多件商品";
        } else {
            if (title.length() > 29) {
                title = title.substring(0, 28) + "...";
            }
        }
        title = title.replaceAll("&", "");
        body = body.replaceAll("&", "");
        log.info("=============body", body);
        log.info("=============title", title);
        payExtraRequest.setSubject(title);
        payExtraRequest.setBody(body);
        payExtraRequest.setClientIp(HttpUtil.getIpAddr());
        if (Objects.nonNull(timeoutCancel)) {
            JSONObject content = JSON.parseObject(timeoutCancel.getContext());
            Integer timeOut = content.getObject("hour", Integer.class);
            payExtraRequest.setExpireTime(timeOut);
        }

        String form = "";
        try {
            form = aliPayProvider.getPayForm(payExtraRequest).getContext().getForm();
        } catch (SbcRuntimeException e) {
            if (e.getErrorCode() != null && e.getErrorCode().equals("K-100203")) {
                //已支付，手动回调
                Operator operator = Operator.builder().ip(HttpUtil.getIpAddr()).adminId("1").name("SYSTEM").platform
                        (Platform.BOSS).build();

                List<TradePayCallBackOnlineDTO> list = new ArrayList<>();
                trades.forEach(i -> {
                    //获取订单信息
                    PayOrderVO payOrder = tradeQueryProvider.getPayOrderById(TradeGetPayOrderByIdRequest.builder()
                            .payOrderId(i.getPayOrderId()).build()).getContext().getPayOrder();
                    TradePayCallBackOnlineDTO dto = TradePayCallBackOnlineDTO.builder()
                            .payOrderOld(KsBeanUtil.convert(payOrder, PayOrderDTO.class))
                            .trade(KsBeanUtil.convert(i, TradeDTO.class))
                            .build();
                    list.add(dto);

                });
                tradeProvider.payCallBackOnlineBatch(new TradePayCallBackOnlineBatchRequest(list, operator));
            }
            throw new SbcRuntimeException(e.getErrorCode(), e.getParams());
        }

        return form;
    }


    /*
     * @Description: app充值支付表单
     * @Param:
     * @Author: Bob->
     * @Date: 2021-08-23 11:12
     */
    @ApiOperation(value = "APP支付宝充值支付", notes = "APP支付宝签名后的参数,返回的数据直接调用appSDK")
    @RequestMapping(value = "app/aliPayRecharge/", method = RequestMethod.POST)
    @LcnTransaction
    @MultiSubmit
    public BaseResponse<String> appRechargeAliPay(@RequestBody @Valid PayMobileRequest payMobileRequest) {
        log.info("====================支付宝支付表单=APP支付宝支付================payMobileRequest :{}", payMobileRequest);
        payMobileRequest.setStoreId(Constant.BOSS_DEFAULT_STORE_ID);

        return BaseResponse.success(this.alipayRechargeUtil(payMobileRequest));
    }

    /*
     * @Description: 支付之前的公共判断条件及数据组装
     * @Param:  payMobileRequest
     * @Author: Bob
     * @Date: 2019-02-22 11:27
     */
    private String alipayRechargeUtil(PayMobileRequest payMobileRequest) {
        String id = payMobileRequest.getTid();
        //订单信息
        WalletRecordVO walletRecordVO = walletRecordProvider.getWalletRecordByRecordNo(WalletRecordRequest.builder().recordNo(id).build()).getContext().getWalletRecordVO();
        if (Objects.isNull(walletRecordVO)) {
            throw new SbcRuntimeException("k-230002", "充值记录不存在!");
        }
        //充值金额,交易金额
        BigDecimal totalPrice = walletRecordVO.getDealPrice();
        PayExtraRequest payExtraRequest = new PayExtraRequest();
        payExtraRequest.setStoreId(payMobileRequest.getStoreId());
        payExtraRequest.setBusinessId(id);
        payExtraRequest.setChannelItemId(payMobileRequest.getChannelItemId());
        payExtraRequest.setTerminal(payMobileRequest.getTerminal());
        if (TerminalType.H5.equals(payMobileRequest.getTerminal())) {
            payExtraRequest.setSuccessUrl(payMobileRequest.getSuccessUrl());
        }
        payExtraRequest.setAmount(totalPrice);
        payExtraRequest.setOpenId(payMobileRequest.getOpenId());


//        //查询商品信息
        VirtualGoodsVO virtualGoodsVO = virtualGoodsQueryProvider.getVirtualGoods(VirtualGoodsRequest.builder().goodsId(Long.valueOf(walletRecordVO.getVirtualGoodsId())).build()).getContext().getVirtualGoods();
        if (Objects.isNull(virtualGoodsVO)) {
            throw new SbcRuntimeException("k-230003", "商品信息不存在!");
        }
        String title = virtualGoodsVO.getGoodsName();
        String body = title + " " + "活动商品";
        title = title.replaceAll("&", "");
        body = body.replaceAll("&", "");
        log.info("=============body", body);
        log.info("=============title", title);
        payExtraRequest.setSubject(title);
        payExtraRequest.setBody(body);
        payExtraRequest.setClientIp(HttpUtil.getIpAddr());
        String form = aliPayProvider.getPayRechargeForm(payExtraRequest).getContext().getForm();
        return form;
    }

    /**
     * 根据不同的支付方式获取微信支付对应的appId
     *
     * @return
     */
    @ApiOperation(value = "根据不同的支付方式获取微信支付对应的appId")
    @RequestMapping(value = "/getAppId/{payGateway}", method = RequestMethod.GET)
    public BaseResponse<Map<String, Object>> getAppId(@PathVariable PayGatewayEnum payGateway) {
        GatewayConfigByGatewayRequest request = new GatewayConfigByGatewayRequest();
        request.setGatewayEnum(payGateway);
        request.setStoreId(commonUtil.getStoreIdWithDefault());
        BaseResponse<PayGatewayConfigResponse> baseResponse = payQueryProvider.getGatewayConfigByGateway(request);
        Map<String, Object> appIdMap = new HashMap<>();
        if (baseResponse.getCode().equals(CommonErrorCode.SUCCESSFUL)) {
            appIdMap.put("appId", baseResponse.getContext().getAppId());
        }
        return BaseResponse.success(appIdMap);
    }

    /**
     * 获取不同渠道对应的openid
     *
     * @param code
     * @return
     */
    @ApiOperation(value = "获取微信支付对应的openId")
    @RequestMapping(value = "/getOpenId/{payGateway}/{code}", method = RequestMethod.GET)
    public BaseResponse<String> getOpenIdByChannel(@PathVariable PayGatewayEnum payGateway, @PathVariable String code) {
        String appId = "";
        String secret = "";
        GatewayConfigByGatewayRequest request = new GatewayConfigByGatewayRequest();
        request.setGatewayEnum(payGateway);
        request.setStoreId(commonUtil.getStoreIdWithDefault());
        BaseResponse<PayGatewayConfigResponse> baseResponse = payQueryProvider.getGatewayConfigByGateway(request);
        if (baseResponse.getCode().equals(CommonErrorCode.SUCCESSFUL)) {
            appId = baseResponse.getContext().getAppId();
            secret = baseResponse.getContext().getSecret();
        }
        GetAccessTokeResponse getAccessTokeResponse = wechatApi.getWeChatAccessToken(appId, secret, code);
        return BaseResponse.success(getAccessTokeResponse.getOpenid());
    }

    /**
     * 小程序通过code获取openId
     *
     * @param code
     * @return
     */
    @ApiOperation(value = "获取小程序微信支付对应的openId")
    @RequestMapping(value = "/getOpenId/littleProgram/{code}", method = RequestMethod.GET)
    public BaseResponse<String> getLittleProgramOpenId(@PathVariable String code) {
        String appId;
        String secret;
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainInfo)) {
            BaseResponse<StoreWechatMiniProgramConfigByCacheResponse> cacheResponse = storeWechatMiniProgramConfigQueryProvider.getCacheByStoreId(StoreWechatMiniProgramConfigByCacheRequest
                    .builder()
                    .storeId(domainInfo.getStoreId())
                    .build());
            if (Objects.nonNull(cacheResponse)
                    && Objects.nonNull(cacheResponse.getContext())
                    && Objects.nonNull(cacheResponse.getContext().getStoreWechatMiniProgramConfigVO())) {
                appId = cacheResponse.getContext().getStoreWechatMiniProgramConfigVO().getAppId();
                secret = cacheResponse.getContext().getStoreWechatMiniProgramConfigVO().getAppSecret();
            } else {
                throw new SbcRuntimeException(CommonErrorCode.METHOD_NOT_ALLOWED);
            }
        } else {
            BaseResponse<MiniProgramSetGetResponse> baseResponse = wechatAuthProvider.getMiniProgramSet();
            if (baseResponse.getCode().equals(CommonErrorCode.SUCCESSFUL)) {
                JSONObject json = JSON.parseObject(baseResponse.getContext().getContext());
                appId = json.getString("appId");
                secret = json.getString("appSecret");
            } else {
                throw new SbcRuntimeException(CommonErrorCode.FAILED);
            }
        }
        LittleProgramAuthResponse getAccessTokeResponse = wechatApi.getLittleProgramAccessToken(appId, secret, code);
        return BaseResponse.success(getAccessTokeResponse.getOpenid());
    }

    @ApiOperation(value = "余额支付")
    @RequestMapping(value = "/balancePay", method = RequestMethod.POST)
    @LcnTransaction
    public BaseResponse balancePay(@RequestBody @Valid PayMobileRequest payMobileRequest) {
        RLock rLock = redissonClient.getFairLock(commonUtil.getOperatorId());
        rLock.lock();
        try {
            PayGatewayConfigResponse gatewayConfigResponse =
                    payQueryProvider.getGatewayConfigByGatewayId(GatewayConfigByGatewayIdRequest.builder()
                            .gatewayId((long) 5)
                            // boss端才有余额支付
                            .storeId(Constants.BOSS_DEFAULT_STORE_ID)
                            .build()).getContext();
            if (gatewayConfigResponse.getPayGateway().getIsOpen().equals(IsOpen.NO)) {
                throw new SbcRuntimeException("K-050407");
            }
            String tradeId;
            if (Objects.nonNull(payMobileRequest.getTid())) {
                tradeId = payMobileRequest.getTid();
            } else {
                tradeId = payMobileRequest.getParentTid();
            }
            List<TradeVO> tradeVOS = payServiceHelper.checkTrades(tradeId);
            // 订单总金额
            BigDecimal totalPrice = tradeVOS.stream()
                    .map(tradeVO -> tradeVO.getTradePrice().getTotalPrice())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);


            //校验密码是否可用
            CustomerGetByIdResponse customerGetByIdResponse = customerQueryProvider.getCustomerById(new
                    CustomerGetByIdRequest(commonUtil.getCustomer().getCustomerId())).getContext();
            if (StringUtils.isBlank(customerGetByIdResponse.getCustomerPayPassword())) {
                throw new SbcRuntimeException(CustomerErrorCode.NO_CUSTOMER_PAY_PASSWORD);
            }
            if (customerGetByIdResponse.getPayErrorTime() != null && customerGetByIdResponse.getPayErrorTime() == 3) {
                Duration duration = Duration.between(customerGetByIdResponse.getPayLockTime(), LocalDateTime.now());
                if (duration.toMinutes() < 30) {
                    //支付密码输错三次，并且锁定时间还未超过30分钟，返回账户冻结错误信息
                    throw new SbcRuntimeException(CustomerErrorCode.CUSTOMER_PAY_LOCK_TIME_ERROR, new Object[]{30 - duration.toMinutes()});
                }
            }

            //校验密码是否正确
            CustomerCheckPayPasswordRequest customerCheckPayPasswordRequest = new CustomerCheckPayPasswordRequest();
            customerCheckPayPasswordRequest.setPayPassword(payMobileRequest.getPayPassword());
            customerCheckPayPasswordRequest.setCustomerId(commonUtil.getCustomer().getCustomerId());
            customerSiteProvider.checkCustomerPayPwd(customerCheckPayPasswordRequest);

            // 处理用户余额, 校验余额是否可用
//            CustomerFundsByCustomerIdResponse fundsByCustomerIdResponse =
//                    customerFundsQueryProvider.getByCustomerId(new CustomerFundsByCustomerIdRequest(
//                            commonUtil.getOperatorId())).getContext();
            BalanceByCustomerIdResponse balanceByCustomerIdResponse = walletQueryProvider.getBalanceByCustomerId(WalletByCustomerIdQueryRequest.builder().customerId(
                    commonUtil.getOperatorId()).build()).getContext();
            if (balanceByCustomerIdResponse.getCustomerWalletVO().getBalance().compareTo(totalPrice) < 0) {
                throw new SbcRuntimeException("K-050408");
            }


            BigDecimal tradeTotalPrice = BigDecimal.ZERO;
            for (TradeVO tradeVO : tradeVOS) {
                tradeTotalPrice = tradeTotalPrice.add(tradeVO.getTradePrice().getTotalPrice());
                AddWalletRecordRecordRequest request = new AddWalletRecordRecordRequest();
                //request.setTradeRemark("余额支付-" + tradeVO.getId());
                request.setTradeRemark(WalletDetailsType.DEDUCTION_ORDER_DEDUCTION.getDesc() + "-" + tradeVO.getId());
                //客户账号
                String customerAccount = customerGetByIdResponse.getCustomerAccount();
                request.setCustomerAccount(customerAccount);
                request.setRelationOrderId(tradeVO.getId());
                request.setTradeType(WalletRecordTradeType.BALANCE_PAY);
                request.setBudgetType(BudgetType.EXPENDITURE);
                request.setDealPrice(tradeVO.getTradePrice().getTotalPrice());
                //request.setRemark("批发支付成功-线上成功");
                request.setRemark(WalletDetailsType.DEDUCTION_ORDER_DEDUCTION.getDesc() + "-" + "批发支付成功-线上成功");
//                request.setDealTime(LocalDateTime.now());
                request.setCurrentBalance(balanceByCustomerIdResponse.getCustomerWalletVO().getBalance());
                request.setTradeState(TradeStateEnum.PAID);
                request.setPayType(1);
                request.setBalance(balanceByCustomerIdResponse.getCustomerWalletVO().getBalance().subtract(tradeVO.getTradePrice().getTotalPrice()));
                walletRecordProvider.addWalletRecord(request);
            }

            //扣除余额
            /*WalletRequest walletRequest =  WalletRequest.builder()
                    .customerId(balanceByCustomerIdResponse.getCustomerWalletVO().getCustomerId())
                    .expenseAmount(totalPrice).build();
            customerWalletProvider.balancePay(walletRequest);*/
//            List<CustomerFundsDetailAddRequest> customerFundsDetailAddRequestList = tradeVOS.stream()
//                    .map(tradeVO -> {
//                        tradeTotalPrice = tradeTotalPrice.add(tradeVO.getTradePrice().getTotalPrice())；
//                        CustomerFundsDetailAddRequest customerFundsDetailAddRequest =
//                                new CustomerFundsDetailAddRequest();
//                        customerFundsDetailAddRequest.setCustomerId(fundsByCustomerIdResponse.getCustomerId());
//                        customerFundsDetailAddRequest.setBusinessId(tradeVO.getId());
//                        customerFundsDetailAddRequest.setFundsType(FundsType.BALANCE_PAY);
//                        customerFundsDetailAddRequest.setReceiptPaymentAmount(tradeVO.getTradePrice().getTotalPrice());
//                        customerFundsDetailAddRequest.setFundsStatus(FundsStatus.YES);
//                        customerFundsDetailAddRequest.setAccountBalance(fundsByCustomerIdResponse.getAccountBalance().subtract(tradeVO.getTradePrice().getTotalPrice()));
//                        customerFundsDetailAddRequest.setSubType(FundsSubType.BALANCE_PAY);
//                        customerFundsDetailAddRequest.setCreateTime(LocalDateTime.now());
//                        return customerFundsDetailAddRequest;
//                    }).collect(Collectors.toList());
//            customerFundsDetailProvider.batchAdd(customerFundsDetailAddRequestList);
            // 新增交易记录
            List<PayTradeRecordRequest> payTradeRecordRequests = tradeVOS.stream()
                    .map(tradeVO -> {
                        PayTradeRecordRequest payTradeRecordRequest = new PayTradeRecordRequest();
                        payTradeRecordRequest.setBusinessId(tradeVO.getId());
                        payTradeRecordRequest.setApplyPrice(tradeVO.getTradePrice().getTotalPrice());
                        payTradeRecordRequest.setPracticalPrice(tradeVO.getTradePrice().getTotalPrice());
                        payTradeRecordRequest.setResult_code("SUCCESS");
                        payTradeRecordRequest.setChannelItemId(21L);
                        return payTradeRecordRequest;
                    }).collect(Collectors.toList());
            payProvider.batchSavePayTradeRecord(payTradeRecordRequests);

            // 支付成功，处理订单
            List<TradePayCallBackOnlineDTO> tradePayCallBackOnlineDTOS = tradeVOS.stream()
                    .map(tradeVO -> {
                        TradePayCallBackOnlineDTO tradePayCallBackOnlineDTO = new TradePayCallBackOnlineDTO();
                        tradePayCallBackOnlineDTO.setTrade(KsBeanUtil.convert(tradeVO, TradeDTO.class));
                        PayOrderVO payOrder = tradeQueryProvider.getPayOrderById(TradeGetPayOrderByIdRequest.builder()
                                .payOrderId(tradeVO.getPayOrderId()).build()).getContext().getPayOrder();
                        tradePayCallBackOnlineDTO.setPayOrderOld(KsBeanUtil.convert(payOrder, PayOrderDTO.class));
                        return tradePayCallBackOnlineDTO;
                    }).collect(Collectors.toList());
            Operator operator = Operator.builder().ip(HttpUtil.getIpAddr()).adminId("-1").name("UNIONB2B")
                    .platform(Platform.THIRD).build();

            tradeProvider.payCallBackOnlineBatch(TradePayCallBackOnlineBatchRequest.builder()
                    .requestList(tradePayCallBackOnlineDTOS)
                    .operator(operator)
                    .build());
            // 余额支付同步供应商订单状态
            this.providerTradePayCallBack(tradeVOS);
        } catch (Exception e) {
            throw e;
        } finally {
            rLock.unlock();
        }
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 组装自提码参数
     */
    private PickUpRecordAddRequest sendPickUpCode(TradeVO trade) {
        String verifyCode = RandomStringUtils.randomNumeric(6);
        return PickUpRecordAddRequest.builder().storeId(trade.getSupplier().getStoreId())
                .tradeId(trade.getId()).pickUpCode(verifyCode).pickUpFlag(DefaultFlag.NO)
                .delFlag(DeleteFlag.NO).contactPhone(trade.getConsignee().getPhone()).createTime(LocalDateTime.now()).build();
    }

    /**
     * 余额支付同步供应商订单状态
     *
     * @param trades
     */
    private void providerTradePayCallBack(List<TradeVO> trades) {
        if (CollectionUtils.isNotEmpty(trades)) {
            trades.forEach(parentTradeVO -> {
                String parentId = parentTradeVO.getId();
                BaseResponse<TradeListByParentIdResponse> supplierListByParentId =
                        providerTradeQueryProvider.getProviderListByParentId(TradeListByParentIdRequest.builder().parentTid(parentId).build());
                if (Objects.nonNull(supplierListByParentId.getContext()) && CollectionUtils.isNotEmpty(supplierListByParentId.getContext().getTradeVOList())) {
                    supplierListByParentId.getContext().getTradeVOList().forEach(childTradeVO -> {
                        childTradeVO.getTradeState().setPayState(PayState.PAID);
                        TradeUpdateRequest tradeUpdateRequest = new TradeUpdateRequest(KsBeanUtil.convert(childTradeVO, TradeUpdateDTO.class));
                        providerTradeProvider.providerUpdate(tradeUpdateRequest);
                    });
                }
            });
        }
    }


    /**
     * 获取可用支付项 —— for好友代付
     *
     * @return
     */
    @ApiOperation(value = "获取可用支付项")
    @RequestMapping(value = "/friend/items/{type}", method = RequestMethod.GET)
    public BaseResponse<List<PayChannelItemVO>> friendItems(@PathVariable String type) {
        GatewayOpenedByStoreIdRequest request = new GatewayOpenedByStoreIdRequest();
        request.setStoreId(Constants.BOSS_DEFAULT_STORE_ID);
        List<PayGatewayConfigVO> payGatewayConfigList = payQueryProvider.listOpenedGatewayConfig(request).getContext()
                .getGatewayConfigVOList();
        List<PayChannelItemVO> itemList = new ArrayList<>();
        payGatewayConfigList.forEach(config -> {
            List<PayChannelItemVO> payChannelItemList = payQueryProvider.listOpenedChannelItemByGatewayName(new
                    OpenedChannelItemRequest(
                    config.getPayGateway().getName(), TerminalType.valueOf(type))).getContext().getPayChannelItemVOList();
            if (CollectionUtils.isNotEmpty(payChannelItemList)) {
                itemList.addAll(payChannelItemList);
            }
        });
        return BaseResponse.success(itemList);
    }

    @ApiOperation(value = "判断是否合并支付")
    @GetMapping(value = "/isMerge/{tid}")
    public BaseResponse<Boolean> isMerge(@PathVariable String tid) {
        List<TradeVO> tradeVOList = tradeQueryProvider.getListByParentId(
                TradeListByParentIdRequest.builder().parentTid(tradeQueryProvider.getById(
                        TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO().getParentId()).build()).getContext().getTradeVOList();
        return BaseResponse.success(tradeVOList.size() > 1 ? true : false);
    }

    /**
     * 好友代付页使用
     *
     * @param tid
     * @return
     */
    @ApiOperation(value = "支付校验", notes = "支付前校验是否已支付成功", httpMethod = "GET")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单号", required = true)
    @RequestMapping(value = "/aliPay/friend/check/{tid}", method = RequestMethod.GET)
    public BaseResponse checkPayStateForFriend(@PathVariable String tid) {
        TradeVO trade = tradeQueryProvider.getById(TradeGetByIdRequest.builder()
                .tid(tid).build()).getContext().getTradeVO();
        //0:未支付
        String flag = "0";
        if (Objects.nonNull(trade)) {
            if (Objects.nonNull(trade.getTradeState())) {
                LocalDateTime orderTimeOut = trade.getOrderTimeOut();
                //已支付
                if (PayState.PAID.equals(trade.getTradeState().getPayState())) {
                    flag = "1";
                }
                //已超过未支付取消订单时间或者已作废
                if (FlowState.VOID.equals(trade.getTradeState().getFlowState()) || Objects.nonNull(orderTimeOut) && orderTimeOut.isBefore(LocalDateTime.now())) {
                    flag = "2";
                }
            }
        }
        return BaseResponse.success(flag);
    }


    /**
     * 好友代付页使用
     *
     * @param tid
     * @return
     */
    @ApiOperation(value = "支付校验", notes = "支付前校验是否已支付成功", httpMethod = "GET")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单号", required = true)
    @RequestMapping(value = "/pile/aliPay/friend/check/{tid}", method = RequestMethod.GET)
    public BaseResponse checkPilePayStateForFriend(@PathVariable String tid) {
        TradeVO trade = pileTradeQueryProvider.getById(TradeGetByIdRequest.builder()
                .tid(tid).build()).getContext().getTradeVO();
        //0:未支付
        String flag = "0";
        if (Objects.nonNull(trade)) {
            if (Objects.nonNull(trade.getTradeState())) {
                LocalDateTime orderTimeOut = trade.getOrderTimeOut();
                //已支付
                if (PayState.PAID.equals(trade.getTradeState().getPayState())) {
                    flag = "1";
                }
                //已超过未支付取消订单时间或者已作废
                if (FlowState.VOID.equals(trade.getTradeState().getFlowState()) || Objects.nonNull(orderTimeOut) && orderTimeOut.isBefore(LocalDateTime.now())) {
                    flag = "2";
                }
            }
        }
        return BaseResponse.success(flag);
    }


    /*
     * @Description: 支付表单 —— for好友代付
     * @Param:
     * @Author: Bob->
     * @Date: 2019-02-01 11:12
     */
    @ApiOperation(value = "H5支付宝支付表单", notes = "该请求需新打开一个空白页，返回的支付宝脚本会自动提交重定向到支付宝收银台", httpMethod = "GET")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "encrypted", value = "base64编码后的支付请求",
            required = true)
    @RequestMapping(value = "/friend/aliPay/{encrypted}", method = RequestMethod.GET)
    @LcnTransaction
    public void aliPayFriend(@PathVariable String encrypted, HttpServletResponse response) {
        String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
        PayMobileRequest payMobileRequest = JSON.parseObject(decrypted, PayMobileRequest.class);
        log.info("====================支付宝支付表单================payMobileRequest :{}", payMobileRequest);

        payMobileRequest.setStoreId(Constants.BOSS_DEFAULT_STORE_ID);
        String form = this.alipayUtil(payMobileRequest);

        try {
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write(form);//直接将完整的表单html输出到页面
            response.getWriter().flush();
            response.getWriter().close();
        } catch (IOException e) {
            // TODO: 2019-01-28 gb支付异常未处理
            log.error("execute alipay has IO exception:{} ", e);
        }
    }

    /**
     * 招商app支付统一下单接口
     *
     * @param weiXinPayRequest
     * @return
     */
    @ApiOperation(value = " 招商app支付统一下单接口", notes = "返回用于app内支付的所需参数")
    @RequestMapping(value = "/cmbPayUnifiedorderForApp", method = RequestMethod.POST)
    @MultiSubmit
    public BaseResponse<Map<String, String>> cmbPayUnifiedorderForApp(@RequestBody @Valid WeiXinPayRequest weiXinPayRequest) {
        CmbPayOrderRequest appRequest = new CmbPayOrderRequest();
        String id = payServiceHelper.getPayBusinessId(weiXinPayRequest.getTid(), weiXinPayRequest.getParentTid());
        String totalPrice = "";
        String body = "";
        if (StringUtils.isNotEmpty(weiXinPayRequest.getTid()) && weiXinPayRequest.getTid().startsWith(GeneratorService._NEW_PILE_PREFIX_TRADE_ID)) {
            List<NewPileTradeVO> trades = payServiceHelper.checkNewPileTrades(id);
            //订单总金额
            totalPrice = payServiceHelper.calcTotalPriceByPennyPile(trades).toString();
            body = payServiceHelper.buildBodyNewPile(trades);
        } else {
            List<TradeVO> trades = payServiceHelper.checkTrades(id);
            //订单总金额
            totalPrice = payServiceHelper.calcTotalPriceByPenny(trades).toString();
            body = payServiceHelper.buildBody(trades);
        }

        appRequest.setBody(body + "订单");
        appRequest.setOut_trade_no(id);
        appRequest.setTotal_fee(totalPrice);
        appRequest.setSpbill_create_ip(HttpUtil.getIpAddr());
        appRequest.setTrade_type(WxPayTradeType.APP.toString());
        appRequest.setStoreId(Constants.BOSS_DEFAULT_STORE_ID);

        //查询后台配置支付超时时间
        List<ConfigVO> configVOList = auditQueryProvider.listTradeConfig().getContext().getConfigVOList();
        ConfigVO timeoutCancel = configVOList.stream().filter(c -> ("order_setting_timeout_cancel").equals(c.getConfigType())).findFirst().orElse(null);
        if (Objects.nonNull(timeoutCancel)) {
            JSONObject content = JSON.parseObject(timeoutCancel.getContext());
            appRequest.setOutTime(String.valueOf(content.getObject("hour", Integer.class)));
        } else {
            appRequest.setOutTime(String.valueOf(30));
        }
        return cmbPayProvider.cmbPayOrder(appRequest);
    }


    @RequestMapping(value = "/cupsClosePayOrder", method = RequestMethod.POST)
    public BaseResponse<String> closePayOrder(@RequestBody @Valid WeiXinPayRequest weiXinPayRequest) {
        BaseResponse<TradeGetByIdResponse> resp = tradeQueryProvider.getOrderById(TradeGetByIdRequest.builder().tid(weiXinPayRequest.getTid()).build());
        TradeVO tradeVO = resp.getContext().getTradeVO();

        String id = StringUtils.isNotBlank(tradeVO.getId()) ? tradeVO.getId() : tradeVO.getParentId();
        String payOrderNo = getPayOrderNo(id);


        Map<String,String> params = new LinkedHashMap<>();
        params.put("payOrderNo",payOrderNo);
        params.put("channelId",weiXinPayRequest.getSourceChannel());
        params.put("storeId",String.valueOf(Constants.BOSS_DEFAULT_STORE_ID));
        return cupsPayProvider.closePayOrder(params);
    }

    @RequestMapping(value = "/wxJSApiClosePayOrder", method = RequestMethod.POST)
    public BaseResponse<WxPayOrderCloseForJSApiResponse> closeWxPayOrderForJSApi(@RequestBody @Valid WxPayOrderCloseForJSApiRequest weiXinCloseRequest) {
        weiXinCloseRequest.setPayOrderNo(getPayOrderNo(weiXinCloseRequest.getBusinessId()));
        return wxPayProvider.closeWxPayOrderForJSApi(weiXinCloseRequest);
    }


    private String getPayOrderNo(String orderCode) {
        PayTradeRecordRequest request = new PayTradeRecordRequest();
        request.setBusinessId(orderCode);
        return payQueryProvider.findByBusinessId(request).getContext().getPayOrderNo();
    }

    /**
     * 银联app支付统一下单接口
     *
     * @param weiXinPayRequest
     * @return
     */
    @ApiOperation(value = " 银联app支付统一下单接口", notes = "返回用于app内支付的所需参数")
    @RequestMapping(value = "/cupsPayUnifiedorderForApp", method = RequestMethod.POST)
    @MultiSubmit
    public BaseResponse<Map<String, String>> cupsPayUnifiedorderForApp(@RequestBody @Valid WeiXinPayRequest weiXinPayRequest) {
        return BaseResponse.error("APP版本太低，请更新您的APP再进行支付，谢谢！");
        /*if (needLockStock(weiXinPayRequest)) {
            //检查订单条目：1 促销活动 2 买商品赠券 3 使用的优惠券已过期
            TradeCheckResponse tradeCheckResponse = tradeQueryProvider.checkTrade(TradeCheckRequest.builder()
                            .parentTid(weiXinPayRequest.getParentTid())
                            .tid(weiXinPayRequest.getTid())
                            .build())
                    .getContext();

            if (tradeCheckResponse.getType() != 0) {
                return BaseResponse.error("很遗憾，您所购买的商品存在库存不足或活动已失效，请稍后再试");
            }
        }

        String id = payServiceHelper.getPayBusinessId(weiXinPayRequest.getTid(), weiXinPayRequest.getParentTid());
        String payOrderNo = "";
        //支付请求体
        String body = "";
        //订单总金额
        String totalPrice = "";
        CmbPayOrderRequest appRequest = new CmbPayOrderRequest();
        //验证财务单是否已经完成，完成不允许在交易
        if ((StringUtils.isNotEmpty(weiXinPayRequest.getTid()) && weiXinPayRequest.getTid().startsWith(GeneratorService._NEW_PILE_PREFIX_TRADE_ID))
            ||(StringUtils.isNotEmpty(weiXinPayRequest.getParentTid()) && weiXinPayRequest.getParentTid().startsWith(GeneratorService._NEW_PILE_PARENT_PREFIX_TRADE_ID))
            ) {
            payOrderNo = payOrderProvider.generateNewPayOrderByOrderCodeNewPile(StringRequest.builder().value(id)
                    .storeId(Constants.BOSS_DEFAULT_STORE_ID)
                    .channelId(weiXinPayRequest.getChannelId()).
                            build()).getContext();

            log.info("====囤货====银联payOrderNo：{}", payOrderNo);
            List<NewPileTradeVO> newPileTradeVOS = payServiceHelper.checkNewPileTrades(id);

            for (NewPileTradeVO vo : newPileTradeVOS) {
                vo.setPayOrderNo(payOrderNo);
                newPileTradeProvider.update(NewPileTradeUpdateRequest.builder().trade(KsBeanUtil.convert(vo, NewPileTradeUpdateDTO.class)).build());
            }
            totalPrice = payServiceHelper.calcTotalPriceByPennyPile(newPileTradeVOS).toString();
            body = payServiceHelper.buildBodyNewPile(newPileTradeVOS);
        } else {
            payOrderNo = payOrderProvider.generateNewPayOrderByOrderCode(StringRequest.builder().value(id)
                    .storeId(Constants.BOSS_DEFAULT_STORE_ID)
                    .channelId(weiXinPayRequest.getChannelId()).
                            build()).getContext();

            log.info("========银联payOrderNo：{}", payOrderNo);
            List<TradeVO> trades = payServiceHelper.checkTrades(id);

            for (TradeVO vo : trades) {
                vo.setPayOrderNo(payOrderNo);
                tradeProvider.update(TradeUpdateRequest.builder().trade(KsBeanUtil.convert(vo, TradeUpdateDTO.class)).build());
            }

            totalPrice = payServiceHelper.calcTotalPriceByPenny(trades).toString();
            body = payServiceHelper.buildBody(trades);
        }
        appRequest.setBody(body + "订单");
        appRequest.setOut_trade_no(id);
        appRequest.setTotal_fee(totalPrice);
        appRequest.setSpbill_create_ip(HttpUtil.getIpAddr());
        appRequest.setTrade_type(WxPayTradeType.APP.toString());
        appRequest.setStoreId(Constants.BOSS_DEFAULT_STORE_ID);
        appRequest.setChannelId(weiXinPayRequest.getChannelId());
        appRequest.setSubOpenId(weiXinPayRequest.getOpenid());
        appRequest.setPayOrderNo(payOrderNo);
        //查询后台配置支付超时时间
        List<ConfigVO> configVOList = auditQueryProvider.listTradeConfig().getContext().getConfigVOList();
        ConfigVO timeoutCancel = configVOList.stream().filter(c -> ("order_setting_timeout_cancel").equals(c.getConfigType())).findFirst().orElse(null);
        if (Objects.nonNull(timeoutCancel)) {
            JSONObject content = JSON.parseObject(timeoutCancel.getContext());
            appRequest.setOutTime(String.valueOf(content.getObject("hour", Integer.class)));
        } else {
            appRequest.setOutTime(String.valueOf(30));
        }

        //囤货和囤货提货不进行支付锁库存
        if (!needLockStock(weiXinPayRequest)) {
            return cupsPayProvider.cupsPayOrder(appRequest);
        }
        //商家入驻囤货提货不进行支付锁库存
        if (StringUtils.isNotBlank(weiXinPayRequest.getParentTid()) &&
                weiXinPayRequest.getParentTid().startsWith(GeneratorService._NEW_PILE_PARENT_PREFIX_TRADE_ID)) {
            return cupsPayProvider.cupsPayOrder(appRequest);
        }

//        1.获取订单分布式锁
//        2. 写入占库存订单表：订单号（唯一索引），确定支付时间，
//        成功进行第2步，失败不再处理
//        3. 锁定本地库存，失败返回
//                第三方付款
//        4. 建立5分钟后取消支付延时任务
        List<TradeVO> tradeVOs = new ArrayList<>();
        if (StringUtils.isNotBlank(weiXinPayRequest.getParentTid())) {
            tradeVOs = tradeQueryProvider.getOrderListByParentId(TradeListByParentIdRequest.builder().parentTid(weiXinPayRequest.getParentTid()).build())
                    .getContext().getTradeVOList();
        } else {
            tradeVOs.add(tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(weiXinPayRequest.getTid()).build()).getContext().getTradeVO());
        }

        List<String> tids = tradeVOs.stream().map(TradeVO::getId).sorted().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(tids)) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "未找到对应的订单号，请检查！");
        }

        List<RLock> rLocks = new ArrayList<>();
        tids.forEach(tid -> {
            RLock rLock = redissonClient.getFairLock(tid);
            rLock.lock();
            rLocks.add(rLock);
        });

        try {
            BaseResponse<Map<String, Integer>> lockStockResp = lockStock(tradeVOs);
            if (!CommonErrorCode.SUCCESSFUL.equals(lockStockResp.getCode())) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "库存锁定失败，请稍后尝试！");
            }
            BaseResponse cupsPayOrderResult = cupsPayProvider.cupsPayOrder(appRequest);
            // 创建取消支付延时任务
            Map<String, Integer> lockResultMap = lockStockResp.getContext();
            if (MapUtils.isNotEmpty(lockResultMap) && lockResultMap.values().stream().allMatch(lockResult -> lockResult == 1)) {
                producerService.createCancelPayDelayTask(tids);
            }
            return cupsPayOrderResult;
        } finally {
            //解锁
            rLocks.forEach(Lock::unlock);
        }*/
    }

    /**
     * 锁定库存
     *
     * @param tradeVOs
     * @return
     */
    private BaseResponse<Map<String, Integer>> lockStock(List<TradeVO> tradeVOs) {
        //收集sku购买信息
        List<GoodsInfoLockStockRequest> goodsInfoBatchLockStockRequests = new ArrayList<>();
        fillBNum(tradeVOs);

        tradeVOs.forEach(tradeVO -> {
            Map<String, BigDecimal> buyCountBySkuMap = new HashMap<>();
            tradeVO.getTradeItems().forEach(tradeItemVO -> {
                buyCountBySkuMap.put(tradeItemVO.getSkuId(), buyCountBySkuMap.getOrDefault(tradeItemVO.getSkuId(), BigDecimal.ZERO).add(tradeItemVO.getBNum()));
            });

            tradeVO.getGifts().forEach(tradeItemVO -> {
                buyCountBySkuMap.put(tradeItemVO.getSkuId(), buyCountBySkuMap.getOrDefault(tradeItemVO.getSkuId(), BigDecimal.ZERO).add(tradeItemVO.getBNum()));
            });

            GoodsInfoLockStockRequest request = new GoodsInfoLockStockRequest();
            request.setStockList(buildStockList(buyCountBySkuMap));
            request.setWareId(tradeVO.getWareId());
            request.setBusinessId(tradeVO.getId());
            goodsInfoBatchLockStockRequests.add(request);
        });

        return goodsInfoProvider.lockStock(goodsInfoBatchLockStockRequests);
    }

    private static void fillBNum(List<TradeVO> tradeVOs) {
        tradeVOs.forEach(tradeVO -> {
            tradeVO.getTradeItems().forEach(tradeItem -> {
                if (Objects.nonNull(tradeItem.getDivisorFlag()) && tradeItem.getDivisorFlag().compareTo(BigDecimal.ZERO) > 0) {
                    tradeItem.setBNum(tradeItem.getDivisorFlag().multiply(BigDecimal.valueOf(tradeItem.getNum())).setScale(2, RoundingMode.DOWN));
                } else {
                    tradeItem.setBNum(BigDecimal.valueOf(tradeItem.getNum()));
                }
            });
            tradeVO.getGifts().forEach(tradeItem -> {
                if (Objects.nonNull(tradeItem.getDivisorFlag()) && tradeItem.getDivisorFlag().compareTo(BigDecimal.ZERO) > 0) {
                    tradeItem.setBNum(tradeItem.getDivisorFlag().multiply(BigDecimal.valueOf(tradeItem.getNum())).setScale(2, RoundingMode.DOWN));
                } else {
                    tradeItem.setBNum(BigDecimal.valueOf(tradeItem.getNum()));
                }
            });
        });
    }

    private List<GoodsInfoLockStockDTO> buildStockList(Map<String, BigDecimal> buyCountBySkuMap) {
        if (MapUtils.isEmpty(buyCountBySkuMap)) {
            return Collections.emptyList();
        }

        return buyCountBySkuMap.entrySet().stream().map(entry -> {
            GoodsInfoLockStockDTO dto = new GoodsInfoLockStockDTO();
            dto.setStock(entry.getValue());
            dto.setGoodsInfoId(entry.getKey());
            return dto;
        }).collect(Collectors.toList());
    }

    @ApiOperation(value = "微信扫码支付统一下单接口获取二维码接口", notes = "返回pc端页面生成二维码所需参数")
    @RequestMapping(value = "/wxPayUnifiedorderForNative", method = RequestMethod.POST)
    public BaseResponse<WxPayForNativeResponse> unifiedorderForNative(@RequestBody @Valid WeiXinPayRequest weiXinPayRequest) {
        WxPayForNativeRequest nativeRequest = new WxPayForNativeRequest();
        String id = payServiceHelper.getPayBusinessId(weiXinPayRequest.getTid(), weiXinPayRequest.getParentTid());
        List<TradeVO> trades = payServiceHelper.checkTrades(id);
        //订单金额
        String totalPrice = payServiceHelper.calcTotalPriceByPenny(trades).toString();
        String payOrderNo = generateNewPayOrderNo(weiXinPayRequest);
        nativeRequest.setPay_order_no(id);
        nativeRequest.setOut_trade_no(payOrderNo);
        nativeRequest.setTotal_fee(totalPrice);
        nativeRequest.setSpbill_create_ip(HttpUtil.getIpAddr());
        String body = payServiceHelper.buildBody(trades);
        TradeVO trade = trades.get(0);
        String productId = trade.getTradeItems().get(0).getSkuId();
        nativeRequest.setBody(body + "订单");
        nativeRequest.setProduct_id(productId);
        nativeRequest.setTrade_type(WxPayTradeType.NATIVE.toString());
        nativeRequest.setStoreId(commonUtil.getStoreIdWithDefault());
        weiXinPayRequest.setChannelId(14L);
        return wxPayProvider.wxPayForNative(nativeRequest);
    }

    /**
     * 查询微信支付订单，支付状态
     * @param orderId
     * @return
     */
    @GetMapping("/queryWxPayOrder/{orderId}")
    public BaseResponse<WxPayOrderDetailReponse> queryWxPayOrder(@PathVariable String orderId) {
        WxPayOrderDetailReponse response = wxPayProvider.getWxPayOrderDetail(WxPayOrderDetailRequest.builder()
                .businessId(orderId)
                .storeId(Constants.BOSS_DEFAULT_STORE_ID)
                .build()).getContext();
        return BaseResponse.success(response);
    }


    @Autowired
    private CcbPayProvider ccbPayProvider;

    /**
     * 建行支付下单
     */
    @ApiOperation(value = "app建行下单支付")
    @RequestMapping(value = "/ccbPayUnifiedorderForApp", method = RequestMethod.POST)
    @MultiSubmit
    public BaseResponse<CcbPayOrderResponse> createPayOrder(@RequestBody @Valid CcbPayRequest ccbPayRequest) {

        ccbPayRequest.setChannelId(32L);

        if (Objects.isNull(ccbPayRequest.getPayType())) {
            ccbPayRequest.setPayType(3);
        }

        String id = payServiceHelper.getPayBusinessId(ccbPayRequest.getTid(), ccbPayRequest.getParentTid());

        boolean needLockStock = !id.startsWith(GeneratorService._NEW_PILE_PREFIX_TRADE_ID) && !id.startsWith(GeneratorService._NEW_PILE_PREFIX_TRADE_PK_ID);

        if (needLockStock) {
            //检查订单条目：1 促销活动 2 买商品赠券 3 使用的优惠券已过期
            TradeCheckResponse tradeCheckResponse = tradeQueryProvider.checkTrade(TradeCheckRequest.builder()
                            .parentTid(ccbPayRequest.getParentTid())
                            .tid(ccbPayRequest.getTid())
                            .build())
                    .getContext();

            if (tradeCheckResponse.getType() != 0) {
                return BaseResponse.error("很遗憾，您所购买的商品存在库存不足或活动已失效，请稍后再试");
            }
        }

        //验证财务单是否已经完成，完成不允许在交易
        String payOrderNo;
        BigDecimal totalPrice;
        Integer clearingDay;
        List<CcbSubPayOrderRequest> subOrderList = new ArrayList<>();
        Integer freightClearingDay = null;
        if (id.startsWith(GeneratorService._NEW_PILE_PREFIX_TRADE_ID)) {
            payOrderNo = payOrderProvider.generateNewPayOrderByOrderCodeNewPile(StringRequest.builder().value(id)
                    .storeId(Constants.BOSS_DEFAULT_STORE_ID)
                    .channelId(ccbPayRequest.getChannelId()).
                    build()).getContext();

            log.info("====囤货====建行payOrderNo：{}", payOrderNo);
            List<NewPileTradeVO> newPileTradeVOS = payServiceHelper.checkNewPileTrades(id);
            for (NewPileTradeVO vo : newPileTradeVOS) {
                vo.setPayOrderNo(payOrderNo);
                newPileTradeProvider.update(NewPileTradeUpdateRequest.builder().trade(KsBeanUtil.convert(vo, NewPileTradeUpdateDTO.class)).build());
            }

            totalPrice = calculateNewPileTotalPrice(newPileTradeVOS);

            clearingDay = newPileTradeVOS.stream().filter(o -> Objects.nonNull(o.getSupplier().getClearingDay())).map(o -> o.getSupplier().getClearingDay()).max(Comparator.comparing(Integer::valueOf)).orElse(null);
            buildPileCcbSubOrdersInfo(id, payOrderNo, subOrderList, newPileTradeVOS, totalPrice);
            boolean existFreight = subOrderList.stream().anyMatch(o -> Objects.equals(CcbSubOrderType.FREIGHT, o.getCommissionFlag()));
            if (existFreight) {
                log.info("订单: {} 中包含运费，设置运费结算日期", id);
                Long days = subOrderList.stream().filter(o -> Objects.equals(CcbSubOrderType.FREIGHT, o.getCommissionFlag())).map(CcbSubPayOrderRequest::getDays).max(Comparator.comparing(Long::valueOf)).orElse(null);
                if (Objects.nonNull(days)) {
                    freightClearingDay = days.intValue();
                }
            }
        } else {
            payOrderNo = payOrderProvider.generateNewPayOrderByOrderCode(StringRequest.builder().value(id)
                    .storeId(Constants.BOSS_DEFAULT_STORE_ID)
                    .channelId(ccbPayRequest.getChannelId()).
                    build()).getContext();

            log.info("========建行payOrderNo：{}", payOrderNo);
            List<TradeVO> trades = payServiceHelper.checkTrades(id);

            for (TradeVO vo : trades) {
                vo.setPayOrderNo(payOrderNo);
                tradeProvider.update(TradeUpdateRequest.builder().trade(KsBeanUtil.convert(vo, TradeUpdateDTO.class)).build());
            }

            totalPrice = calculateTotalPrice(trades);

            clearingDay = trades.stream().filter(o -> Objects.nonNull(o.getSupplier().getClearingDay())).map(o -> o.getSupplier().getClearingDay()).max(Comparator.comparing(Integer::valueOf)).orElse(null);
            buildCcbSubOrdersInfo(id, payOrderNo, subOrderList, trades, totalPrice);
            boolean existFreight = subOrderList.stream().anyMatch(o -> Objects.equals(CcbSubOrderType.FREIGHT, o.getCommissionFlag()));
            if (existFreight) {
                log.info("订单: {} 中包含运费，设置运费结算日期", id);
                Long days = subOrderList.stream().filter(o -> Objects.equals(CcbSubOrderType.FREIGHT, o.getCommissionFlag())).map(CcbSubPayOrderRequest::getDays).max(Comparator.comparing(Long::valueOf)).orElse(null);
                if (Objects.nonNull(days)) {
                    freightClearingDay = days.intValue();
                }
            }
        }

        CcbPayOrderRequest request = new CcbPayOrderRequest();
        request.setMainOrderNo(payOrderNo);
        request.setOrderAmount(totalPrice);
        request.setTxnAmt(totalPrice);
        request.setSubOrderList(subOrderList);
        request.setClientIp(HttpUtil.getIpAddr());
        request.setChannelId(ccbPayRequest.getChannelId());
        request.setPayOrderNo(payOrderNo);
        request.setBusinessId(id);
        request.setJsCode(ccbPayRequest.getJsCode());
        request.setPayType(ccbPayRequest.getPayType());

        clearingDay = Objects.isNull(clearingDay) ? freightClearingDay : clearingDay;
        if (Objects.nonNull(clearingDay)) {
            if (Objects.nonNull(freightClearingDay) && clearingDay < freightClearingDay) {
                clearingDay = freightClearingDay;
            }
            request.setClrgDt(DateUtil.format(LocalDateTime.now().plusDays(clearingDay - 1), DateUtil.FMT_TIME_5));
            log.info("建行支付设置结算日期：{}，订单号：{}", request.getClrgDt(), id);
        }
        //囤货和囤货提货不进行支付锁库存
        if (!needLockStock) {
           return ccbPayProvider.ccbPayOrder(request);
        }

        //        1.获取订单分布式锁
//        2. 写入占库存订单表：订单号（唯一索引），确定支付时间，
//        成功进行第2步，失败不再处理
//        3. 锁定本地库存，失败返回
//                第三方付款
//        4. 建立5分钟后取消支付延时任务
        List<TradeVO> tradeVOs = new ArrayList<>();
        if (StringUtils.isNotBlank(ccbPayRequest.getParentTid())) {
            tradeVOs = tradeQueryProvider.getOrderListByParentId(TradeListByParentIdRequest.builder().parentTid(ccbPayRequest.getParentTid()).build())
                    .getContext().getTradeVOList();
        } else {
            tradeVOs.add(tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(ccbPayRequest.getTid()).build()).getContext().getTradeVO());
        }

        List<String> tids = tradeVOs.stream().map(TradeVO::getId).sorted().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(tids)) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "未找到对应的订单号，请检查！");
        }

        //预售订单不进行支付锁库存
        for (TradeVO tradeVO : tradeVOs){
            if (tradeVO.getPresellFlag()){
                return ccbPayProvider.ccbPayOrder(request);
            }
        }

        List<RLock> rLocks = new ArrayList<>();
        tids.forEach(tid -> {
            RLock rLock = redissonClient.getFairLock(tid);
            rLock.lock();
            rLocks.add(rLock);
        });

        try {
            BaseResponse<Map<String, Integer>> lockStockResp = lockStock(tradeVOs);
            if (!CommonErrorCode.SUCCESSFUL.equals(lockStockResp.getCode())) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "库存锁定失败，请稍后尝试！");
            }
            BaseResponse<CcbPayOrderResponse> ccbResult = ccbPayProvider.ccbPayOrder(request);
            // 创建取消支付延时任务
            Map<String, Integer> lockResultMap = lockStockResp.getContext();
            if (MapUtils.isNotEmpty(lockResultMap) && lockResultMap.values().stream().allMatch(lockResult -> lockResult == 1)) {
                producerService.createCancelPayDelayTask(tids);
            }
            return ccbResult;
        } finally {
            //解锁
            rLocks.forEach(Lock::unlock);
        }

    }

    private static BigDecimal calculateTotalPrice(List<TradeVO> trades) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (TradeVO trade : trades) {
            if (TradeActivityTypeEnum.NEWPICKTRADE.toActivityType().equals(trade.getActivityType())) {
                totalPrice = totalPrice.add(trade.getTradePrice().getDeliveryPrice())
                        .setScale(2, RoundingMode.DOWN);
            } else {
                totalPrice = totalPrice.add(
                        trade.getTradePrice().getDeliveryPrice()
                                .add(trade.getTradeItems().stream()
                                        .filter(tradeItem -> tradeItem.getSplitPrice() != null && tradeItem.getSplitPrice().compareTo(BigDecimal.ZERO) > 0)
                                        .map(TradeItemVO::getSplitPrice)
                                        .reduce(BigDecimal::add).orElse(BigDecimal.ZERO))
                                .add(trade.getTradePrice().getPackingPrice())
                                // .add(trade.getTradePrice().getVillageAddliveryPrice())
                                .setScale(2, RoundingMode.DOWN)
                );
            }
        }
        return totalPrice;
    }

    private static BigDecimal calculateNewPileTotalPrice(List<NewPileTradeVO> newPileTradeVOS) {
        return newPileTradeVOS.stream().map(
                i -> i.getTradePrice().getDeliveryPrice().add(
                                i.getTradeItems().stream()
                                        .filter(tradeItem -> tradeItem.getSplitPrice() != null && tradeItem.getSplitPrice().compareTo(BigDecimal.ZERO) > 0)
                                        .map(TradeItemVO::getSplitPrice)
                                        .reduce(BigDecimal::add).orElse(BigDecimal.ZERO)
                        ).add(i.getTradePrice().getPackingPrice())
                        // .add(i.getTradePrice().getVillageAddliveryPrice())
                        .setScale(2, RoundingMode.DOWN)).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void buildCcbSubOrdersInfo(String id, String payOrderNo, List<CcbSubPayOrderRequest> subOrderList, List<TradeVO> trades, BigDecimal totalPrice) {
        BigDecimal totalCommission = BigDecimal.ZERO;
        BigDecimal totalFreight = BigDecimal.ZERO;
        BigDecimal totalMktPrice = BigDecimal.ZERO;
        BigDecimal totalFreightCommission = BigDecimal.ZERO;
        BigDecimal totalExtra = BigDecimal.ZERO;

        for (TradeVO vo : trades) {
            // 订单金额
            BigDecimal amt;
            if(TradeActivityTypeEnum.NEWPICKTRADE.toActivityType().equals(vo.getActivityType())){
                amt = vo.getTradePrice().getDeliveryPrice().setScale(2, RoundingMode.DOWN);
            }else{
                amt = vo.getTradePrice().getDeliveryPrice()
                        .add(vo.getTradeItems().stream()
                                .filter(tradeItem -> tradeItem.getSplitPrice() != null && tradeItem.getSplitPrice().compareTo(BigDecimal.ZERO) > 0)
                                .map(TradeItemVO::getSplitPrice)
                                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO))
                        .add(vo.getTradePrice().getPackingPrice())
                        // .add(vo.getTradePrice().getVillageAddliveryPrice())
                        .setScale(2, RoundingMode.DOWN);

            }

            BigDecimal ratio = vo.getSupplier().getShareRatio();
            String ccbMerchantNumber = vo.getSupplier().getCcbMerchantNumber();
            String supplierName = vo.getSupplier().getSupplierName();
            if (Objects.isNull(ratio) || Objects.isNull(ccbMerchantNumber)) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "商家:" + supplierName + ",建行信息未录入");
            }

            // 配送方式
            DeliverWay deliverWay = vo.getDeliverWay();
            // 总佣金
            BigDecimal commission;
            // 配送到店运费
            BigDecimal deliveryPrice = BigDecimal.ZERO;
            // 加收费用
            BigDecimal extraPrice = BigDecimal.ZERO;

            if (DeliverWay.isCcbSubBill(deliverWay)) {
                deliveryPrice = vo.getTradePrice().getDeliveryPrice().setScale(2, RoundingMode.DOWN);
                if (amt.compareTo(deliveryPrice) <= 0) {
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单：" + vo.getId() + "，总金额：" + amt.toPlainString() + " ，应大于运费金额：" + deliveryPrice.toPlainString());
                }
                BigDecimal villageAddliveryPrice = vo.getTradePrice().getVillageAddliveryPrice();
                if (Objects.nonNull(villageAddliveryPrice) && villageAddliveryPrice.compareTo(BigDecimal.ZERO) > 0) {
                    extraPrice = villageAddliveryPrice.setScale(2, RoundingMode.DOWN);
                    deliveryPrice = deliveryPrice.subtract(extraPrice);
                }
            }

            // 订单佣金
            commission = amt.multiply(ratio).setScale(2, RoundingMode.UP);
            /*if (commission.compareTo(amt.subtract(deliveryPrice)) >= 0) {
                commission = BigDecimal.ZERO;
            }*/

            BigDecimal freight = BigDecimal.ZERO;
            BigDecimal freightCommission = BigDecimal.ZERO;
            String freightCcbMktNum = null;
            BigDecimal deliveryPriceCommission = BigDecimal.ZERO;
            BigDecimal freightRatio = BigDecimal.ZERO;
            BigDecimal extra = BigDecimal.ZERO;
            BigDecimal extraPriceCommission = BigDecimal.ZERO;
            BigDecimal extraCommission = BigDecimal.ZERO;
            Long days = null;
            // 配送到店 计算运费
            if (DeliverWay.isCcbSubBill(deliverWay) && deliveryPrice.compareTo(BigDecimal.ZERO) > 0) {
                // vo.getLogisticsCompanyInfo().get..
                TradeDeliverVO deliverVO = vo.getTradeDelivers().stream().findFirst().orElse(null);
                if (Objects.isNull(deliverVO)) {
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单：" + vo.getId() + "，获取获取物流信息失败");
                }
                BigDecimal deliveryRatio = deliverVO.getLogistics().getShareRatio();
                freightCcbMktNum = deliverVO.getLogistics().getCcbMerchantNumber();
                String finalPeriod = deliverVO.getLogistics().getFinalPeriod();
                if (Objects.isNull(deliveryRatio) || Objects.isNull(freightCcbMktNum)) {
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单：" + vo.getId() + "，获取承运商信息失败");
                }
                deliveryRatio = deliveryRatio.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
                if (ratio.compareTo(deliveryRatio) >= 0) {
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单：" + vo.getId() + "，总佣金比例应小于运费佣金比例");
                }
                if (StringUtils.isBlank(finalPeriod) || !finalPeriod.matches("-?\\d+(\\.\\d+)?")) {
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单：" + vo.getId() + "，获取承运商分账周期失败");
                }
                days = Long.valueOf(finalPeriod);
                Boolean verify = ccbPayProvider.validCcbMerchantNo(freightCcbMktNum).getContext();
                if (Objects.isNull(verify) || !verify) {
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单：" + vo.getId() + "，承运商商家编码不存在");
                }

                // 运费
                freightRatio = new BigDecimal("1").subtract(deliveryRatio);
                freight = deliveryPrice.multiply(freightRatio).setScale(2, RoundingMode.HALF_UP);

                if (freight.compareTo(deliveryPrice) < 0) {
                    // 运费佣金
                    deliveryPriceCommission = deliveryPrice.multiply(ratio).setScale(2, RoundingMode.UP);
                    if (deliveryPriceCommission.compareTo(deliveryPrice) >= 0) {
                        deliveryPriceCommission = BigDecimal.ZERO;
                    }
                    freightCommission = deliveryPrice.subtract(deliveryPriceCommission).subtract(freight);
                }

                // 运费加收
                if (extraPrice.compareTo(BigDecimal.ZERO) > 0) {
                    extra = extraPrice.multiply(freightRatio).setScale(2, RoundingMode.HALF_UP);
                    if (extra.compareTo(extraPrice) < 0) {
                        // 运费佣金
                        extraPriceCommission = extraPrice.multiply(ratio).setScale(2, RoundingMode.UP);
                        if (extraPriceCommission.compareTo(extraPrice) >= 0) {
                            extraPriceCommission = BigDecimal.ZERO;
                        }
                        if (extraPriceCommission.add(deliveryPriceCommission).compareTo(commission) > 0) {
                            extraPriceCommission = commission.subtract(deliveryPriceCommission);
                        }
                        extraCommission = extraPrice.subtract(extraPriceCommission).subtract(extra);
                    }
                }

            }

            // 商家金额
            BigDecimal mktPrice = amt.subtract(commission).subtract(freight).subtract(freightCommission).subtract(extra).subtract(extraCommission);

            if (mktPrice.compareTo(new BigDecimal("0.01")) < 0) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单：[" + vo.getId() +"]应付金额太小，无法进行分账计算");
            }

            // 子订单
            CcbSubPayOrderRequest subOrder = new CcbSubPayOrderRequest();
            subOrder.setMktMrchtId(ccbMerchantNumber);
            subOrder.setCmdtyOrderNo(vo.getId() + RandomStringUtils.randomNumeric(5));
            subOrder.setOrderAmount(mktPrice);
            subOrder.setTxnAmt(mktPrice);
            subOrder.setTid(vo.getId());
            subOrder.setFreight(freight);
            subOrder.setFreightCommission(freightCommission);
            subOrder.setExtra(extra);
            subOrder.setExtraCommission(extraCommission);
            subOrder.setCommission(commission);
            subOrder.setTotalAmt(amt);
            BigDecimal mktRatio = commission.compareTo(BigDecimal.ZERO) == 0 ? new BigDecimal("1") : new BigDecimal("1").subtract(ratio);
            subOrder.setRatio(mktRatio);
            subOrder.setCommissionFlag(CcbSubOrderType.MERCHANT);
            subOrderList.add(subOrder);

            if (freight.compareTo(BigDecimal.ZERO) > 0) {
                // 配送到店运费
                CcbSubPayOrderRequest freightSubOrder = new CcbSubPayOrderRequest();
                freightSubOrder.setMktMrchtId(freightCcbMktNum);
                freightSubOrder.setCmdtyOrderNo(vo.getId() + RandomStringUtils.randomNumeric(5));
                freightSubOrder.setOrderAmount(freight);
                freightSubOrder.setTxnAmt(freight);
                freightSubOrder.setTid(vo.getId());
                freightSubOrder.setFreight(freight);
                freightSubOrder.setFreightCommission(freightCommission);
                freightSubOrder.setCommission(deliveryPriceCommission);
                freightSubOrder.setTotalAmt(deliveryPrice);
                freightSubOrder.setRatio(freightCommission.compareTo(BigDecimal.ZERO) == 0 ? new BigDecimal("1") : freightRatio);
                freightSubOrder.setCommissionFlag(CcbSubOrderType.FREIGHT);
                freightSubOrder.setDays(days);
                subOrderList.add(freightSubOrder);
            }

            if (extra.compareTo(BigDecimal.ZERO) > 0) {
                // 运费加收
                CcbSubPayOrderRequest extraSubOrder = new CcbSubPayOrderRequest();
                extraSubOrder.setMktMrchtId(freightCcbMktNum);
                extraSubOrder.setCmdtyOrderNo(vo.getId() + RandomStringUtils.randomNumeric(5));
                extraSubOrder.setOrderAmount(extra);
                extraSubOrder.setTxnAmt(extra);
                extraSubOrder.setTid(vo.getId());
                extraSubOrder.setExtra(extra);
                extraSubOrder.setExtraCommission(extraCommission);
                extraSubOrder.setCommission(extraPriceCommission);
                extraSubOrder.setTotalAmt(extraPrice);
                extraSubOrder.setRatio(freightCommission.compareTo(BigDecimal.ZERO) == 0 ? new BigDecimal("1") : freightRatio);
                extraSubOrder.setCommissionFlag(CcbSubOrderType.EXTRA);
                extraSubOrder.setDays(days);
                subOrderList.add(extraSubOrder);
            }

            // 汇总
            totalCommission = totalCommission.add(commission);
            totalFreight = totalFreight.add(freight);
            totalFreightCommission = totalFreightCommission.add(freightCommission).add(extraCommission);
            totalMktPrice = totalMktPrice.add(mktPrice);
            totalExtra = totalExtra.add(extra);
        }

        BigDecimal calculateTotalPrice = totalMktPrice
                .add(totalCommission)
                .add(totalFreight)
                .add(totalFreightCommission)
                .add(totalExtra);


        if (calculateTotalPrice.compareTo(totalPrice) != 0) {
            log.info("totalMktPrice:{}, totalCommission：{}, totalFreight:{},totalFreightCommission:{},totalExtra:{}"
                    , totalMktPrice, totalCommission, totalFreight, totalFreightCommission, totalExtra );
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "计算分账金额错误");
        }

        if (totalCommission.compareTo(BigDecimal.ZERO) > 0) {
            CcbSubPayOrderRequest commissionSub = new CcbSubPayOrderRequest();
            commissionSub.setTid(id);
            commissionSub.setCmdtyOrderNo(payOrderNo + RandomStringUtils.randomNumeric(5));
            commissionSub.setOrderAmount(totalCommission);
            commissionSub.setTxnAmt(totalCommission);
            commissionSub.setCommissionFlag(CcbSubOrderType.COMMISSION);
            subOrderList.add(commissionSub);
        }

        if (totalFreightCommission.compareTo(BigDecimal.ZERO) > 0) {
            CcbSubPayOrderRequest freightSub = new CcbSubPayOrderRequest();
            freightSub.setTid(id);
            freightSub.setCmdtyOrderNo(payOrderNo + RandomStringUtils.randomNumeric(5));
            freightSub.setOrderAmount(totalFreightCommission);
            freightSub.setTxnAmt(totalFreightCommission);
            freightSub.setCommissionFlag(CcbSubOrderType.FREIGHT_COMMISSION);
            subOrderList.add(freightSub);
        }
    }

    private void buildPileCcbSubOrdersInfo(String id, String payOrderNo, List<CcbSubPayOrderRequest> subOrderList, List<NewPileTradeVO> newPileTradeVOS, BigDecimal totalPrice) {
        BigDecimal totalCommission = BigDecimal.ZERO;
        BigDecimal totalFreight = BigDecimal.ZERO;
        BigDecimal totalMktPrice = BigDecimal.ZERO;
        BigDecimal totalFreightCommission = BigDecimal.ZERO;
        BigDecimal totalExtra = BigDecimal.ZERO;

        for (NewPileTradeVO vo : newPileTradeVOS) {
            // 订单金额
            BigDecimal amt = vo.getTradePrice().getDeliveryPrice().add(vo.getTradeItems().stream()
                            .filter(tradeItem -> tradeItem.getSplitPrice() != null && tradeItem.getSplitPrice().compareTo(BigDecimal.ZERO) > 0)
                            .map(TradeItemVO::getSplitPrice)
                            .reduce(BigDecimal::add).orElse(BigDecimal.ZERO)).add(vo.getTradePrice().getPackingPrice())
                            // .add(vo.getTradePrice().getVillageAddliveryPrice())
                    .setScale(2, RoundingMode.DOWN);


            BigDecimal ratio = vo.getSupplier().getShareRatio();
            String ccbMerchantNumber = vo.getSupplier().getCcbMerchantNumber();
            String supplierName = vo.getSupplier().getSupplierName();
            if (Objects.isNull(ratio) || Objects.isNull(ccbMerchantNumber)) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "商家:" + supplierName + ",建行信息未录入");
            }

            // 配送方式
            DeliverWay deliverWay = vo.getDeliverWay();
            // 总佣金
            BigDecimal commission;
            // 配送到店运费 增加 配送快递到家自费
            BigDecimal deliveryPrice = BigDecimal.ZERO;
            // 加收费用
            BigDecimal extraPrice = BigDecimal.ZERO;
            if (DeliverWay.isCcbSubBill(deliverWay)) {
                deliveryPrice = vo.getTradePrice().getDeliveryPrice().setScale(2, RoundingMode.DOWN);
                if (amt.compareTo(deliveryPrice) <= 0) {
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单：" + vo.getId() + "，总金额：" + amt.toPlainString() + " ，应大于运费金额：" + deliveryPrice.toPlainString());
                }
                BigDecimal villageAddliveryPrice = vo.getTradePrice().getVillageAddliveryPrice();
                if (Objects.nonNull(villageAddliveryPrice) && villageAddliveryPrice.compareTo(BigDecimal.ZERO) > 0) {
                    extraPrice = villageAddliveryPrice.setScale(2, RoundingMode.DOWN);
                    deliveryPrice = deliveryPrice.subtract(extraPrice);
                }
            }

            // 订单佣金
            commission = amt.multiply(ratio).setScale(2, RoundingMode.UP);
            /*if (commission.compareTo(amt.subtract(deliveryPrice)) >= 0) {
                commission = BigDecimal.ZERO;
            }*/

            BigDecimal freight = BigDecimal.ZERO;
            BigDecimal freightCommission = BigDecimal.ZERO;
            String freightCcbMktNum = null;
            BigDecimal deliveryPriceCommission = BigDecimal.ZERO;
            BigDecimal freightRatio = BigDecimal.ZERO;
            BigDecimal extra = BigDecimal.ZERO;
            BigDecimal extraPriceCommission = BigDecimal.ZERO;
            BigDecimal extraCommission = BigDecimal.ZERO;
            Long days = null;
            // 配送到店 计算运费
            if (DeliverWay.isCcbSubBill(deliverWay) && deliveryPrice.compareTo(BigDecimal.ZERO) > 0) {
                // vo.getLogisticsCompanyInfo().get..
                TradeDeliverVO deliverVO = vo.getTradeDelivers().stream().findFirst().orElse(null);
                if (Objects.isNull(deliverVO)) {
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单：" + vo.getId() + "，获取获取物流信息失败");
                }
                BigDecimal deliveryRatio = deliverVO.getLogistics().getShareRatio();
                freightCcbMktNum = deliverVO.getLogistics().getCcbMerchantNumber();
                String finalPeriod = deliverVO.getLogistics().getFinalPeriod();
                if (Objects.isNull(deliveryRatio) || Objects.isNull(freightCcbMktNum)) {
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单：" + vo.getId() + "，获取承运商信息失败");
                }
                deliveryRatio = deliveryRatio.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
                if (ratio.compareTo(deliveryRatio) >= 0) {
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单：" + vo.getId() + "，总佣金比例应小于运费佣金比例");
                }
                if (StringUtils.isBlank(finalPeriod) || !finalPeriod.matches("-?\\d+(\\.\\d+)?")) {
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单：" + vo.getId() + "，获取承运商分账周期失败");
                }
                days = Long.valueOf(finalPeriod);
                Boolean verify = ccbPayProvider.validCcbMerchantNo(freightCcbMktNum).getContext();
                if (Objects.isNull(verify) || !verify) {
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单：" + vo.getId() + "，承运商商家编码不存在");
                }

                // 运费
                freightRatio = new BigDecimal("1").subtract(deliveryRatio);
                freight = deliveryPrice.multiply(freightRatio).setScale(2, RoundingMode.HALF_UP);

                if (freight.compareTo(deliveryPrice) < 0) {
                    // 运费佣金
                    deliveryPriceCommission = deliveryPrice.multiply(ratio).setScale(2, RoundingMode.UP);
                    if (deliveryPriceCommission.compareTo(deliveryPrice) >= 0) {
                        deliveryPriceCommission = BigDecimal.ZERO;
                    }
                    freightCommission = deliveryPrice.subtract(deliveryPriceCommission).subtract(freight);
                }

                // 运费加收
                if (extraPrice.compareTo(BigDecimal.ZERO) > 0) {
                    extra = extraPrice.multiply(freightRatio).setScale(2, RoundingMode.HALF_UP);
                    if (extra.compareTo(extraPrice) < 0) {
                        // 运费佣金
                        extraPriceCommission = extraPrice.multiply(ratio).setScale(2, RoundingMode.UP);
                        if (extraPriceCommission.compareTo(extraPrice) >= 0) {
                            extraPriceCommission = BigDecimal.ZERO;
                        }
                        if (extraPriceCommission.add(deliveryPriceCommission).compareTo(commission) > 0) {
                            extraPriceCommission = commission.subtract(deliveryPriceCommission);
                        }
                        extraCommission = extraPrice.subtract(extraPriceCommission).subtract(extra);
                    }
                }
            }

            // 商家金额
            BigDecimal mktPrice = amt.subtract(commission).subtract(freight).subtract(freightCommission).subtract(freightCommission).subtract(extra).subtract(extraCommission);

            if (mktPrice.compareTo(new BigDecimal("0.01")) < 0) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单：[" + vo.getId() +"]应付金额太小，无法进行分账计算");
            }

            // 子订单
            CcbSubPayOrderRequest subOrder = new CcbSubPayOrderRequest();
            subOrder.setMktMrchtId(ccbMerchantNumber);
            subOrder.setCmdtyOrderNo(vo.getId() + RandomStringUtils.randomNumeric(5));
            subOrder.setOrderAmount(mktPrice);
            subOrder.setTxnAmt(mktPrice);
            subOrder.setTid(vo.getId());
            subOrder.setFreight(freight);
            subOrder.setFreightCommission(freightCommission);
            subOrder.setExtra(extra);
            subOrder.setExtraCommission(extraCommission);
            subOrder.setCommission(commission);
            subOrder.setTotalAmt(amt);
            BigDecimal mktRatio = commission.compareTo(BigDecimal.ZERO) == 0 ? new BigDecimal("1") : new BigDecimal("1").subtract(ratio);
            subOrder.setRatio(mktRatio);
            subOrder.setCommissionFlag(CcbSubOrderType.MERCHANT);
            subOrderList.add(subOrder);

            if (freight.compareTo(BigDecimal.ZERO) > 0) {
                // 配送到店运费
                CcbSubPayOrderRequest freightSubOrder = new CcbSubPayOrderRequest();
                freightSubOrder.setMktMrchtId(freightCcbMktNum);
                freightSubOrder.setCmdtyOrderNo(vo.getId() + RandomStringUtils.randomNumeric(5));
                freightSubOrder.setOrderAmount(freight);
                freightSubOrder.setTxnAmt(freight);
                freightSubOrder.setTid(vo.getId());
                freightSubOrder.setFreight(freight);
                freightSubOrder.setFreightCommission(freightCommission);
                freightSubOrder.setCommission(deliveryPriceCommission);
                freightSubOrder.setTotalAmt(deliveryPrice);
                freightSubOrder.setRatio(freightCommission.compareTo(BigDecimal.ZERO) == 0 ? new BigDecimal("1") : freightRatio);
                freightSubOrder.setCommissionFlag(CcbSubOrderType.FREIGHT);
                freightSubOrder.setDays(days);
                subOrderList.add(freightSubOrder);
            }

            if (extra.compareTo(BigDecimal.ZERO) > 0) {
                // 运费加收
                CcbSubPayOrderRequest extraSubOrder = new CcbSubPayOrderRequest();
                extraSubOrder.setMktMrchtId(freightCcbMktNum);
                extraSubOrder.setCmdtyOrderNo(vo.getId() + RandomStringUtils.randomNumeric(5));
                extraSubOrder.setOrderAmount(extra);
                extraSubOrder.setTxnAmt(extra);
                extraSubOrder.setTid(vo.getId());
                extraSubOrder.setExtra(extra);
                extraSubOrder.setExtraCommission(extraCommission);
                extraSubOrder.setCommission(extraPriceCommission);
                extraSubOrder.setTotalAmt(extraPrice);
                extraSubOrder.setRatio(freightCommission.compareTo(BigDecimal.ZERO) == 0 ? new BigDecimal("1") : freightRatio);
                extraSubOrder.setCommissionFlag(CcbSubOrderType.EXTRA);
                extraSubOrder.setDays(days);
                subOrderList.add(extraSubOrder);
            }

            // 汇总
            totalCommission = totalCommission.add(commission);
            totalFreight = totalFreight.add(freight);
            totalFreightCommission = totalFreightCommission.add(freightCommission).add(extraCommission);
            totalMktPrice = totalMktPrice.add(mktPrice);
            totalExtra = totalExtra.add(extra);
        }
        BigDecimal calculateTotalPrice = totalMktPrice
                .add(totalCommission)
                .add(totalFreight)
                .add(totalFreightCommission)
                .add(totalExtra);
        if (calculateTotalPrice.compareTo(totalPrice) != 0) {
            log.info("totalMktPrice:{}, totalCommission：{}, totalFreight:{},totalFreightCommission:{},totalExtra:{}"
                    , totalMktPrice, totalCommission, totalFreight, totalFreightCommission, totalExtra);
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "计算分账金额错误");
        }

        if (totalCommission.compareTo(BigDecimal.ZERO) > 0) {
            CcbSubPayOrderRequest commissionSub = new CcbSubPayOrderRequest();
            commissionSub.setTid(id);
            commissionSub.setCmdtyOrderNo(payOrderNo + RandomStringUtils.randomNumeric(5));
            commissionSub.setOrderAmount(totalCommission);
            commissionSub.setTxnAmt(totalCommission);
            commissionSub.setCommissionFlag(CcbSubOrderType.COMMISSION);
            subOrderList.add(commissionSub);
        }

        if (totalFreightCommission.compareTo(BigDecimal.ZERO) > 0) {
            CcbSubPayOrderRequest freightSub = new CcbSubPayOrderRequest();
            freightSub.setTid(id);
            freightSub.setCmdtyOrderNo(payOrderNo + RandomStringUtils.randomNumeric(5));
            freightSub.setOrderAmount(totalFreightCommission);
            freightSub.setTxnAmt(totalFreightCommission);
            freightSub.setCommissionFlag(CcbSubOrderType.FREIGHT_COMMISSION);
            subOrderList.add(freightSub);
        }
    }
    

    /**
     *
     *
     * 建行支付宝小程序订单查询
     * @param tradeNo
     * @return
     */
    @ApiOperation(value = "建行支付宝小程序订单查询")
    @GetMapping(value = "/ccbAlipayTradeQuery/{tradeNo}")
    public BaseResponse<AlipayTradeQueryResponse> ccbAlipayTradeQuery(@PathVariable("tradeNo") String tradeNo) {
        return ccbPayProvider.ccbAlipayTradeQuery(tradeNo);
    }

}
