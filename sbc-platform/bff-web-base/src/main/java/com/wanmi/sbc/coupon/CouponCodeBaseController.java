package com.wanmi.sbc.coupon;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.coupon.request.CouponCheckoutBaseRequest;
import com.wanmi.sbc.coupon.request.CouponFetchBaseRequest;
import com.wanmi.sbc.coupon.response.CouponCodePageResponseNew;
import com.wanmi.sbc.coupon.service.CouponService;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressResponse;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponActivityProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponActivityQueryProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCodeProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCodeQueryProvider;
import com.wanmi.sbc.marketing.api.request.coupon.BuyGoodsOrFullOrderSendCouponRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponActivityGetRegisteredRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponActivityPageRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponActivityPageResponse;
import com.wanmi.sbc.marketing.api.request.coupon.CouponCheckoutRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponCodePageRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponCodeQueryRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponFetchRequest;
import com.wanmi.sbc.marketing.api.request.coupon.LongNotOrderSendCouponGroupRequest;
import com.wanmi.sbc.marketing.api.response.coupon.BuyGoodsOrFullOrderSendCouponResponse;
import com.wanmi.sbc.marketing.api.response.coupon.CouponCheckoutResponse;
import com.wanmi.sbc.marketing.api.response.coupon.CouponCodePageResponse;
import com.wanmi.sbc.marketing.api.response.coupon.CouponNewCustomerResponse;
import com.wanmi.sbc.marketing.api.response.coupon.GetCouponGroupResponse;
import com.wanmi.sbc.marketing.bean.dto.CouponCodeDTO;
import com.wanmi.sbc.marketing.bean.enums.CouponActivityType;
import com.wanmi.sbc.marketing.bean.enums.MarketingStatus;
import com.wanmi.sbc.marketing.bean.vo.CouponActivityVO;
import com.wanmi.sbc.marketing.bean.vo.CouponCodeVO;
import com.wanmi.sbc.marketing.bean.vo.StoreCouponCodeVO;
import com.wanmi.sbc.shopcart.api.provider.cart.ShopCartQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.shopcart.api.request.purchase.PurchaseListRequest;
import com.wanmi.sbc.order.api.request.trade.TradePageCriteriaRequest;
import com.wanmi.sbc.shopcart.api.response.purchase.PurchaseListResponse;
import com.wanmi.sbc.order.api.response.trade.TradePageCriteriaResponse;
import com.wanmi.sbc.order.bean.dto.TradeQueryDTO;
import com.wanmi.sbc.order.bean.dto.TradeStateDTO;
import com.wanmi.sbc.order.bean.enums.PayState;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.util.CommonUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * Created by CHENLI on 2018/9/21.
 */
@RestController
@RequestMapping("/coupon-code")
@Api(tags = "CouponCodeBaseController", description = "S2B web公用-我的优惠券API")
@Slf4j
public class CouponCodeBaseController {


    @Autowired
    private CouponCodeQueryProvider couponCodeQueryProvider;

    @Autowired
    private CouponCodeProvider couponCodeProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private CouponActivityProvider couponActivityProvider;

    @Autowired
    private CouponActivityQueryProvider couponActivityQueryProvider;

    @Autowired
    private ShopCartQueryProvider shopCartQueryProvider;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;
    
    @Autowired
    private CouponService couponService;

    /**
     * APP / H5 查询我的优惠券
     * @param request
     * @return
     */
    @ApiOperation(value = "APP/H5查询我的优惠券")
    @RequestMapping(value = "/my-coupon", method = RequestMethod.POST)
    public BaseResponse<CouponCodePageResponse> listMyCouponList(@RequestBody CouponCodePageRequest request){
        request.setCustomerId(commonUtil.getOperatorId());
        List<Long> wareIds = new ArrayList<>(2);
        wareIds.add(-1L);
        wareIds.add(commonUtil.getWareId(HttpUtil.getRequest()));
        request.setWareIds(wareIds);

        DomainStoreRelaVO domainStoreRelaVO = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainStoreRelaVO)) {
            request.setStoreId(domainStoreRelaVO.getStoreId());
        }
        return couponCodeQueryProvider.page(request);
    }
    
    /**
     * APP / H5 查询我的优惠券（商家入驻版本）
     * @param request
     * @return
     */
    @ApiOperation(value = "APP/H5查询我的优惠券")
    @RequestMapping(value = "/my-coupon-new", method = RequestMethod.POST)
	public BaseResponse<CouponCodePageResponseNew> myCouponListNew(@RequestBody CouponCodePageRequest request) {
		log.info("====进入/coupon-code/my-coupon-new====");

		request.setCustomerId(commonUtil.getOperatorId());
		List<Long> wareIds = new ArrayList<>(2);
		wareIds.add(-1L);
		wareIds.add(commonUtil.getWareId(HttpUtil.getRequest()));
		request.setWareIds(wareIds);

		DomainStoreRelaVO domainStoreRelaVO = commonUtil.getDomainInfo();
		if (Objects.nonNull(domainStoreRelaVO)) {
			request.setStoreId(domainStoreRelaVO.getStoreId());
		}

		// 查出所有优惠券
		request.setPageSize(9999);
		BaseResponse<CouponCodePageResponse> page = couponCodeQueryProvider.page(request);

		List<CouponCodeVO> couponViews = page.getContext().getCouponCodeVos().getContent();
		// 组装成app端需要的数据格式
		List<StoreCouponCodeVO> resList = couponService.parseCouponCodeVOList(couponViews);
		return BaseResponse.success(CouponCodePageResponseNew.builder().storeCouponCodeVOList(resList).build());
	}



    /**
     * 根据活动和优惠券领券
     * @param baseRequest
     * @return
     */
    @ApiOperation(value = "根据活动和优惠券领券")
    @RequestMapping(value = "/fetch-coupon", method = RequestMethod.POST)
    public BaseResponse customerFetchCoupon(@Valid @RequestBody CouponFetchBaseRequest baseRequest){
        CouponFetchRequest request = new CouponFetchRequest();
        KsBeanUtil.copyProperties(baseRequest, request);
        request.setCustomerId(commonUtil.getOperatorId());

        DomainStoreRelaVO domainStoreRelaVO = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainStoreRelaVO)) {
            request.setStoreId(domainStoreRelaVO.getStoreId());
        }
        return couponCodeProvider.fetch(request);
    }

    /**
     * 使用优惠券选择时的后台处理
     * @param baseRequest
     * @return
     */
    @ApiOperation(value = "使用优惠券选择时的后台处理")
    @RequestMapping(value = "/checkout-coupons", method = RequestMethod.POST)
    public BaseResponse<CouponCheckoutResponse> checkoutCoupons(@Valid @RequestBody CouponCheckoutBaseRequest baseRequest) {
        CouponCheckoutRequest request = new CouponCheckoutRequest();
        KsBeanUtil.copyProperties(baseRequest, request);
        CustomerVO customer = commonUtil.getCustomer();
        if (StringUtils.isNotBlank(customer.getParentCustomerId())){
            request.setCustomerId(customer.getParentCustomerId());
        }else {
            request.setCustomerId(customer.getCustomerId());
        }
        DomainStoreRelaVO domainStoreRelaVO = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainStoreRelaVO)) {
            request.setStoreId(domainStoreRelaVO.getStoreId());
        }
        return couponCodeQueryProvider.checkout(request);
    }

    /**
     * 购买指定商品/订单满额领取优惠券
     * @param request
     * @return
     */
    @ApiOperation(value = "购买指定商品/订单满额领取优惠券")
    @RequestMapping(value = "/getGoodsSendCouponGroup", method = RequestMethod.POST)
    public BaseResponse<BuyGoodsOrFullOrderSendCouponResponse> getBuyGoodsOrFullOrderSendCouponGroup(@Valid @RequestBody BuyGoodsOrFullOrderSendCouponRequest request){
        List<GetCouponGroupResponse> couponList = new ArrayList<>();
        //购买指定商品赠券
        List<BuyGoodsOrFullOrderSendCouponResponse> goodsResponse = couponActivityProvider.getBuyGoodsSendCouponGroup(request).getContext();
        if (Objects.nonNull(goodsResponse)){
            goodsResponse.forEach(item -> couponList.addAll(item.getCouponList()));
        }
        //订单满额赠券
        BuyGoodsOrFullOrderSendCouponResponse orderResponse = couponActivityProvider.getFullOrderSendCouponGroup(request).getContext();
        if (CollectionUtils.isNotEmpty(orderResponse.getCouponList())){
            couponList.addAll(orderResponse.getCouponList());
        }
        return BaseResponse.success(BuyGoodsOrFullOrderSendCouponResponse.builder().couponList(couponList).build());
    }

    /**
     * 久未下单是否有资格领取优惠券
     * @return
     */
    @ApiOperation(value = "久未下单是否有资格领取优惠券")
    @PostMapping(value = "/sendCoupon/longNotOrder")
    public BaseResponse longNotOrder(){
        //获取会员
        CustomerVO customer = commonUtil.getCustomer();
        log.info("FANG--------customer"+ JSON.toJSONString(customer));

        //1.检查用户购物车是否有商品
        // 采购单列表
        PurchaseListRequest requestShopCart = new PurchaseListRequest();
        requestShopCart.setCustomerId(customer.getCustomerId());
        requestShopCart.setInviteeId(commonUtil.getPurchaseInviteeId());
        requestShopCart.setGoodsInfoIds(new ArrayList<>());
        requestShopCart.setWareId(1l);
        requestShopCart.setMatchWareHouseFlag(true);
        requestShopCart.setCustomer(customer);
        requestShopCart.setIsRefresh(true);

        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainInfo)) {
            requestShopCart.setCompanyInfoId(domainInfo.getCompanyInfoId());
        }
        //通过客户收货地址和商品指定区域设置商品状态
        //根据用户ID得到收货地址
        CustomerDeliveryAddressResponse deliveryAddress = commonUtil.getDeliveryAddress();
        if(Objects.nonNull(deliveryAddress)){
            requestShopCart.setProvinceId(deliveryAddress.getProvinceId());
            requestShopCart.setCityId(deliveryAddress.getCityId());
        }
        BaseResponse<PurchaseListResponse> response = shopCartQueryProvider.devanningPurchaseInfo(requestShopCart);
        if(Objects.isNull(response)){
            log.info("response------------购物车无商品");
            return BaseResponse.SUCCESSFUL();
        }
        PurchaseListResponse context1 = response.getContext();
        if(Objects.isNull(context1)){
            log.info("context1------------购物车无商品");
            return BaseResponse.SUCCESSFUL();
        }
        MicroServicePage<GoodsInfoVO> goodsInfoPage = context1.getGoodsInfoPage();
        if(Objects.isNull(goodsInfoPage)){
            log.info("goodsInfoPage------------购物车无商品");
            return BaseResponse.SUCCESSFUL();
        }
        List<GoodsInfoVO> shopCartList = goodsInfoPage.getContent();

        //购物车无商品  返回
        if(CollectionUtils.isEmpty(shopCartList)){
            log.info("shopCartList------------购物车无商品");
            return BaseResponse.SUCCESSFUL();
        }
        //2. 检查是否有活动进行
        CouponActivityPageRequest request = new CouponActivityPageRequest();
        request.setCouponActivityType(CouponActivityType.LONG_NOT_ORDER);
        request.setQueryTab(MarketingStatus.STARTED);
        BaseResponse<CouponActivityPageResponse> page = couponActivityQueryProvider.page(request);
        if(Objects.isNull(page)){
            log.info("page------------无正在进行中久未下单活动11");
            return BaseResponse.SUCCESSFUL();
        }
        CouponActivityPageResponse context = page.getContext();
        if(Objects.isNull(context) || Objects.isNull(context.getCouponActivityVOPage())){
            //无正在进行中久未下单活动 返回
            log.info("activityContent------------无正在进行中久未下单活动11");
            return BaseResponse.SUCCESSFUL();
        }
        MicroServicePage<CouponActivityVO> couponActivityVOPage = context.getCouponActivityVOPage();
        List<CouponActivityVO> activityContent = couponActivityVOPage.getContent();
        log.info("FANG--------activityContent"+activityContent);
        if(CollectionUtils.isEmpty(activityContent)){
            //无正在进行中久未下单活动 返回
            log.info("activityContent------------无正在进行中久未下单活动");
            return BaseResponse.SUCCESSFUL();
        }
        //3.检查最近下单时间
        TradeQueryDTO orderReq = new TradeQueryDTO();
        TradeStateDTO readeState = new TradeStateDTO();
        readeState.setPayState(PayState.PAID);
        orderReq.setTradeState(readeState);
        orderReq.setCustomerIds(new Object[]{customer.getCustomerId()});
        orderReq.putSort("createTime", "desc");
        BaseResponse<TradePageCriteriaResponse> tradePageCriteriaResponseBaseResponse = tradeQueryProvider.pageCriteria(TradePageCriteriaRequest.builder()
                .tradePageDTO(orderReq).build());
        if(Objects.isNull(tradePageCriteriaResponseBaseResponse)){
            log.info("tradePageCriteriaResponseBaseResponse------------无支付订单11");
        }
        TradePageCriteriaResponse tradeContext = tradePageCriteriaResponseBaseResponse.getContext();
        if(Objects.isNull(tradeContext) || Objects.isNull(tradeContext.getTradePage())){
            log.info("activityContent------------无支付订单11");
            return BaseResponse.SUCCESSFUL();
        }
        List<TradeVO> content = tradeContext.getTradePage().getContent();
        log.info("FANG--------activityContent"+activityContent);
        if(CollectionUtils.isEmpty(content)){
            log.info("activityContent------------无支付订单");
            return BaseResponse.SUCCESSFUL();
        }
        TradeVO tradeVO = content.get(0);
        //得到可以发券的活动
        List<CouponActivityVO> conformActivity = new ArrayList<>();
        activityContent.forEach(var ->{
            LocalDateTime createTime = tradeVO.getTradeState().getCreateTime();
            LocalDateTime localDateTime = createTime.plusDays(var.getLongNotOrder());
            if(localDateTime.isBefore(LocalDateTime.now())){
                conformActivity.add(var);
            }
        });

        LongNotOrderSendCouponGroupRequest sendCouponGroupRequest = new LongNotOrderSendCouponGroupRequest();
        sendCouponGroupRequest.setActivitys(conformActivity);
        sendCouponGroupRequest.setCustomerId(customer.getCustomerId());
        //4.发券及领券检查
        log.info("FANG--------sendCouponGroupRequest"+sendCouponGroupRequest);
        return couponActivityProvider.getLongNotOrderSendCouponGroup(sendCouponGroupRequest);
    }


    /**
     * 判断是否新人优惠券弹窗
     * @return
     */
    @ApiOperation(value = "判断是否新人优惠券弹窗")
    @RequestMapping(value = "/new-customer/popover",method = RequestMethod.GET)
    public BaseResponse<CouponNewCustomerResponse> newCustomerPopover(){
        CouponNewCustomerResponse response = new CouponNewCustomerResponse();

        //获取正在进行的注册赠券活动信息
        CouponActivityVO couponActivityVO = couponActivityQueryProvider.get(CouponActivityGetRegisteredRequest.builder().
                activityType(CouponActivityType.REGISTERED_COUPON).build()).getContext();

        //用户登录，判断用户是否新用户
        if (Objects.nonNull(commonUtil.getOperatorId())){
            if (Objects.nonNull(couponActivityVO)){
                //获取当前用户是否拥有注册活动优惠券
                List<CouponCodeDTO> couponCodeList = couponCodeQueryProvider.listCouponCodeByCondition(CouponCodeQueryRequest.builder()
                        .customerId(commonUtil.getOperatorId())
                        .activityId(couponActivityVO.getActivityId())
                        .delFlag(DeleteFlag.NO)
                        .useStatus(DefaultFlag.NO)
                        .notExpire(true)
                        .build()).getContext().getCouponCodeList();

                if (CollectionUtils.isNotEmpty(couponCodeList)){
                    if(Objects.nonNull(couponActivityVO.getImageUrl())){
                        response.setPopupFlag(true);
                        response.setImageUrl(couponActivityVO.getImageUrl());
                    }
                }
            }
        }else{
            if (Objects.nonNull(couponActivityVO)) {
                //用户没有登录，判断是否设置了优惠券弹窗地址
                if (Objects.nonNull(couponActivityVO.getImageUrl())) {
                    response.setPopupFlag(true);
                    response.setImageUrl(couponActivityVO.getImageUrl());
                }
            }
        }

        return BaseResponse.success(response);
    }

}
