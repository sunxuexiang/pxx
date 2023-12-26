package com.wanmi.sbc.shopcart;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.DistributeChannel;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.SiteResultCode;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressResponse;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.distribute.DistributionCacheService;
import com.wanmi.sbc.distribute.DistributionService;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.info.BulkGoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.info.BulkGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.price.GoodsIntervalPriceProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByIdsRequest;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.marketing.api.provider.market.MarketingQueryProvider;

import com.wanmi.sbc.shopcart.api.provider.cart.BulkShopCartProvider;
import com.wanmi.sbc.shopcart.api.provider.cart.BulkShopCartQueryProvider;
import com.wanmi.sbc.shopcart.api.request.purchase.*;
import com.wanmi.sbc.shopcart.api.response.purchase.*;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

/**
 * 采购单Controller
 * Created by daiyitian on 17/4/12.
 */
@Api(tags = "BulkShopCartBaseController", description = "（正常下单）购物车服务API")
@RestController
@RequestMapping("/bulk/shopscart")
@Validated
@Slf4j
public class BulkShopCartBaseController {

    @Autowired
    private BulkShopCartQueryProvider bulkShopCartQueryProvider;

    @Autowired
    private BulkShopCartProvider bulkShopCartProvider;

    @Autowired
    private BulkGoodsInfoProvider bulkGoodsInfoProvider;

    @Autowired
    private BulkGoodsInfoQueryProvider bulkGoodsInfoQueryProvider;

    @Autowired
    private GoodsIntervalPriceProvider goodsIntervalPriceProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private DistributionService distributionService;

    @Autowired
    private DistributionCacheService distributionCacheService;

    @Autowired
    private DevanningGoodsInfoProvider devanningGoodsInfoProvider;

    @Autowired
    private MarketingQueryProvider marketingQueryProvider;

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;

    @Autowired
    private RedisService redisService;

    /**
     * 获取散批购物车列表
     * 购物车列表 ｜ 1
     * /shopCartsAndCach
     * @return 采购单
     */
    @ApiOperation(value = "获取散批购物车列表")
    @RequestMapping(value = "/queryCart", method = RequestMethod.POST)
    public BaseResponse<MarketingGroupCardResponse> queryCart(@RequestBody PurchaseListRequest request, HttpServletRequest httpRequest) {
        this.builsPurchaseListRequestParam(request, httpRequest);
        log.info("============devanningInfoView：" + JSONObject.toJSONString(request));
        MarketingGroupCardResponse response = bulkShopCartQueryProvider.purchaseInfoCache(request).getContext();
        return BaseResponse.success(response);
    }

    private void builsPurchaseListRequestParam(PurchaseListRequest request, HttpServletRequest httpRequest){

        Long bulkWareId = commonUtil.getBulkWareId(HttpUtil.getRequest());

        //获取会员
        CustomerVO customer = commonUtil.getCustomer();
        // 采购单列表
        request.setCustomerId(customer.getCustomerId());
        request.setInviteeId(commonUtil.getPurchaseInviteeId());
//        request.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
        request.setWareId(commonUtil.getBulkWareId(HttpUtil.getRequest()));
        if(Objects.nonNull(bulkWareId)){
            request.setBulkWareId(String.valueOf(bulkWareId));
        } else {
            request.setBulkWareId("");
        }
        request.setCustomer(customer);

        request.setLocationCityId(commonUtil.getCityId(httpRequest));
        request.setLocationProvinceId(commonUtil.getProvinceId(httpRequest));
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainInfo)) {
            request.setCompanyInfoId(domainInfo.getCompanyInfoId());
        }
    }

    /**
     * 查询选择商品店铺营销
     *
     * 购物车选中 ｜ 购物车全选 ｜ 购物车反选
     * @return 查询选择商品店铺营销
     */
    @ApiOperation(value = "选中购物车中的商品项")
    @RequestMapping(value = "/checkedCartGoods", method = RequestMethod.POST)
    public BaseResponse  checkedCartGoods(@RequestBody CheckedBulkCartRequest request) {
        request.setCustomerId(commonUtil.getOperatorId());
        request.setWareId(commonUtil.getBulkWareId(HttpUtil.getRequest()));
        if (!StringUtils.isNotBlank(request.getType())){
            throw new RuntimeException("必传字段未传");
        }
        bulkShopCartProvider.updateCheckStaues(request);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 删除采购单 缓存删除
     * 删除购物车条目 ｜ 2
     *  /newshopCart
     * @param request 数据
     * @return 结果
     */
    @ApiOperation(value = "删除采购单")
    @RequestMapping(value = "/deleteCartGoods", method = RequestMethod.DELETE)
    public BaseResponse devanningDeletenew(@RequestBody PurchaseDeleteRequest request) {
        this.buildPurchaseDeleteRequest(request);
        bulkShopCartProvider.devanningDeleteCache(request);
        return BaseResponse.SUCCESSFUL();
    }

    private void buildPurchaseDeleteRequest(PurchaseDeleteRequest request){
        request.setCustomerId(commonUtil.getOperatorId());
        if (CollectionUtils.isEmpty(request.getGoodsInfoIds()) || StringUtils.isBlank(request.getCustomerId())) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
        }
        request.setInviteeId(commonUtil.getPurchaseInviteeId());
    }


    /**
     * 调整数量
     * 购物车调整数量 ｜ 3
     * /newshopCart
     * @param request 数据
     * @return 结果
     */
    @ApiOperation(value = "调整数量")
    @RequestMapping(value = "/updateCartGoods", method = RequestMethod.PUT)
    public BaseResponse updateCartGoods(@RequestBody @Valid PurchaseUpdateNumRequest request) {
        this.checkPurchaseUpdateNumRequestParam(request);
        bulkShopCartProvider.updateDevanningNumNew(request);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 新增采购单
     *
     * @param request 数据
     * @return 结果
     */
    @ApiOperation(value = "新增采购单")
    @RequestMapping(value = "/devanning/newshopCart", method = RequestMethod.POST)
    public BaseResponse newdevanningAdd(@RequestBody @Valid PurchaseSaveRequest request) {
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        Long wareId = commonUtil.getBulkWareId(HttpUtil.getRequest());
        if (Objects.nonNull(domainInfo)) {
            request.setSaasStatus(Boolean.TRUE);
            request.setStoreId(domainInfo.getStoreId());
        }
        request.setCustomerId(commonUtil.getOperatorId());
        if (StringUtils.isBlank(request.getCustomerId()) || StringUtils.isBlank(request.getGoodsInfoId())) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
        }
        request.setWareId(wareId);
        request.setInviteeId(commonUtil.getPurchaseInviteeId());
        List<String>  list =new LinkedList<>();
        list.add(request.getGoodsInfoId());

        GoodsInfoVO goodsInfoVO = bulkGoodsInfoQueryProvider.listByIds(GoodsInfoListByIdsRequest.builder().goodsInfoIds(list).wareId(wareId).build()).getContext()
                .getGoodsInfos()
                .stream()
                .findFirst()
                .orElse(null);

        if (Objects.isNull(goodsInfoVO)){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "当前定位区域不支持销售该商品");
        }
        Long devanningGoodsInfoVOWareId = Optional.ofNullable(goodsInfoVO.getWareId()).orElse(null);
        if(Objects.nonNull(devanningGoodsInfoVOWareId) && !Objects.equals(wareId, devanningGoodsInfoVOWareId)){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "当前定位区域不支持销售该商品");
        }
        request.setGoodsInfoId(goodsInfoVO.getGoodsInfoId());
        request.setCityId(commonUtil.getCityId(HttpUtil.getRequest()));
        request.setProvinceId(commonUtil.getProvinceId(HttpUtil.getRequest()));
        bulkShopCartProvider.newsave(request);
        return BaseResponse.SUCCESSFUL();
    }

    private void checkPurchaseUpdateNumRequestParam(PurchaseUpdateNumRequest request){
        request.setCustomerId(commonUtil.getOperatorId());
        Long bulkWareId = commonUtil.getBulkWareId(HttpUtil.getRequest());
        if (StringUtils.isBlank(request.getCustomerId()) || StringUtils.isEmpty(request.getGoodsInfoId()) || Objects.isNull(bulkWareId)) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
        }
        request.setWareId(bulkWareId);
        request.setInviteeId(commonUtil.getPurchaseInviteeId());
        request.setProvinceId(commonUtil.getProvinceId(HttpUtil.getRequest()));
        request.setCityId(commonUtil.getCityId(HttpUtil.getRequest()));
    }

    /**
     * 批量新增采购单(拆箱之后的)
     * 详情页加购 ｜
     * /newbatchAdd
     * @param request
     * @return
     */
    @ApiOperation(value = "批量新增采购单")
    @RequestMapping(value = "/addCartGoods", method = RequestMethod.POST)
    public BaseResponse addCartGoods(@RequestBody PurchaseBatchSaveRequest request,HttpServletRequest httpRequest) {
        request.setCustomerId(commonUtil.getOperatorId());
        Long bulkWareId = commonUtil.getBulkWareId(HttpUtil.getRequest());
        if (StringUtils.isBlank(request.getCustomerId()) || request.getGoodsInfos() == null || Objects.isNull(bulkWareId)) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
        }
        CustomerDeliveryAddressResponse finalDeliveryAddress =commonUtil.getProvinceCity(httpRequest);
        if(finalDeliveryAddress==null){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"请确认您的手机有没有打开定位");
        }
        request.setCityId(finalDeliveryAddress.getCityId());
        request.setProvinceId(finalDeliveryAddress.getProvinceId());
        request.setWareId(commonUtil.getBulkWareId(HttpUtil.getRequest()));

        request.setInviteeId(commonUtil.getPurchaseInviteeId());
        bulkShopCartProvider.batchSaveNewDevanning(request);
        return BaseResponse.SUCCESSFUL();
    }

    //*****************************************************************************

    /**
     * 清除失效商品
     *
     * @return
     */
    @ApiOperation(value = "清除失效商品")
    @RequestMapping(value = "/clearLoseGoods", method = RequestMethod.DELETE)
    public BaseResponse clearLoseGoods() {
        PurchaseClearLoseGoodsRequest purchaseClearLoseGoodsRequest = new PurchaseClearLoseGoodsRequest();
        purchaseClearLoseGoodsRequest.setUserId(commonUtil.getOperatorId());

        DistributeChannel channel = commonUtil.getDistributeChannel();
        channel.setInviteeId(commonUtil.getPurchaseInviteeId());
        purchaseClearLoseGoodsRequest.setDistributeChannel(channel);
        bulkShopCartProvider.clearLoseGoods(purchaseClearLoseGoodsRequest);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 采购单商品移入收藏夹
     *
     * @param queryRequest
     * @return
     */
    @ApiOperation(value = "采购单商品移入收藏夹")
    @RequestMapping(value = "/addFollow", method = RequestMethod.PUT)
    public BaseResponse addFollow(@RequestBody PurchaseAddFollowRequest queryRequest) {
        queryRequest.setCustomerId(commonUtil.getOperatorId());

        queryRequest.setInviteeId(commonUtil.getPurchaseInviteeId());
        bulkShopCartProvider.addFollow(queryRequest);
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "校验限购数量")
    @RequestMapping(value = "/checkShopCartNum",method = RequestMethod.POST)
    public BaseResponse<CheckPurchaseNumResponse> checkPurchaseNum(@RequestBody CheckPurchaseNumRequest request){
        return bulkShopCartQueryProvider.checkPurchaseNum(request);
    }
}
