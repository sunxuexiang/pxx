package com.wanmi.sbc.marketing.api.provider.market;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.marketing.api.request.market.*;
import com.wanmi.sbc.marketing.api.request.market.latest.MarketingByGoodsIdRequest;
import com.wanmi.sbc.marketing.api.request.market.latest.MarketingCalculatorRequest;
import com.wanmi.sbc.marketing.api.request.market.latest.MarketingCardGroupRequest;
import com.wanmi.sbc.marketing.api.request.market.latest.MarketingEffectiveRequest;
import com.wanmi.sbc.marketing.api.request.plugin.MarketingTradeBatchWrapperRequest;
import com.wanmi.sbc.marketing.api.response.market.*;
import com.wanmi.sbc.marketing.api.response.plugin.MarketingTradeBatchWrapperResponse;
import com.wanmi.sbc.marketing.bean.vo.MarketingSuitDetialVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @Author: ZhangLingKe
 * @Description: 营销查询接口Feign客户端
 * @Date: 2018-11-16 16:56
 */
@FeignClient(value = "${application.marketing.name}", url="${feign.url.marketing:#{null}}", contextId = "MarketingQueryProvider")
public interface MarketingQueryProvider {

    /**
     * 订单营销批量处理
     * @param request 包含多个营销处理结构 {@link MarketingTradeBatchWrapperRequest}
     * @return 处理结果 {@link MarketingTradeBatchWrapperResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/trade/batch-wrapper")
    BaseResponse<MarketingTradeBatchWrapperResponse> batchWrapper(@RequestBody @Valid MarketingTradeBatchWrapperRequest request);

    /**
     * 促销分组
     * @param request
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/marking-group-list")
    BaseResponse<MarketingGroupCardResponse> goodsGroupByMarketing(@RequestBody @Valid MarketingCardGroupRequest request);

    /**
     * 单个商品计算最优的营销活动
     * @param request
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/single-marking-group-list")
    BaseResponse<MarketingGroupCardResponse> singleMarketingGroupList(@RequestBody @Valid MarketingCardGroupRequest request);
    /**
     * 计算单个商品的最优营销，未达到门槛返回Null
     * @param request
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/optimal-marketing-single-goods")
    BaseResponse<MarketingCalculatorResultResponse> optimalMarketingSingleGoods(@RequestBody @Valid MarketingCalculatorRequest request);

    /**
     * 通过传入商品集合查出商品对应的营销
     * @param request
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/marking-goodsinfo-list")
    BaseResponse<MarketingByGoodsInfoIdResponse> getMarketingByGoodsInfo(@RequestBody @Valid MarketingByGoodsIdRequest request);

    /**
     * 通过营销种类查询存在的SKU
     * @param request 查询参数 {@link ExistsSkuByMarketingTypeRequest}
     * @return sku编号列表 {@link List}
     */
    @PostMapping("/marketing/${application.marketing.version}/query-exists-sku-by-marketingtype")
    BaseResponse<List<String>> queryExistsSkuByMarketingType(@RequestBody @Valid ExistsSkuByMarketingTypeRequest request);

    /**
     * 通过营销种类查询存在的SKU
     * @param request 查询参数 {@link ExistsSkuByMarketingTypeRequest}
     * @return sku编号列表 {@link List}
     */
    @PostMapping("/marketing/${application.marketing.version}/query-exists-marketing-by-marketingtype")
    BaseResponse<List<String>> queryExistsMarketingByMarketingType(@RequestBody @Valid ExistsSkuByMarketingTypeRequest request);

    /**
     * 通过营销种类查询存在的SKU
     * @param request 查询参数 {@link ExistsSkuByMarketingTypeRequest}
     * @return sku编号列表 {@link List}
     */
    @PostMapping("/marketing/${application.marketing.version}/query-exists-sku-by-marketingtype-full-order")
    BaseResponse<List<String>> queryExistsSkuByMarketingTypeFullOrder(@RequestBody @Valid ExistsSkuByMarketingTypeRequest request);


    /**
     * 分页查询营销数据
     * @param marketingPageRequest 分页查询参数 {@link MarketingPageRequest}
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/page")
    BaseResponse<MarketingPageResponse> page(@RequestBody @Valid MarketingPageRequest marketingPageRequest);

    /**
     * 分页查询营销数据
     * @param marketingPageRequest 分页查询参数 {@link MarketingPageRequest}
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/page-for-suit")
    BaseResponse<MarketingPageResponse> pageForSuit(@RequestBody @Valid MarketingPageRequest marketingPageRequest);

    /**
     * 根据id获取营销实体
     * @param getByIdRequest 唯一编号参数 {@link MarketingGetByIdRequest}
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/get-by-id")
    BaseResponse<MarketingGetByIdResponse> getById(@RequestBody @Valid MarketingGetByIdRequest getByIdRequest);

    /**
     * 商家端根据id获取营销实体
     * @param getByIdRequest 唯一编号参数 {@link MarketingGetByIdRequest}
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/get-by-id-for-supplier")
    BaseResponse<MarketingGetByIdForSupplierResponse> getByIdForSupplier(@RequestBody @Valid MarketingGetByIdByIdRequest getByIdRequest);

    /**
     * 会员端根据id获取营销实体
     * @param getByIdRequest 唯一编号参数 {@link MarketingGetByIdRequest}
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/get-by-id-for-customer")
    BaseResponse<MarketingGetByIdForCustomerResponse> getByIdForCustomer(@RequestBody @Valid MarketingGetByIdRequest getByIdRequest);

    /**
     * 根据多个id获取多个营销实体
     * @param queryByIdsRequest 唯一编号参数列表 {@link MarketingGetByIdRequest}
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/query-by-ids")
    BaseResponse<MarketingQueryByIdsResponse> queryByIds(@RequestBody @Valid MarketingQueryByIdsRequest queryByIdsRequest);

    /**
     * 根据id获取营销对应的商品信息
     * @param getByIdRequest 唯一编号参数 {@link MarketingGetByIdRequest}
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/get-goods-by-id")
    BaseResponse<MarketingGetGoodsByIdResponse> getGoodsById(@RequestBody @Valid MarketingGetByIdRequest getByIdRequest);

    /**
     * 获取验证进行中的营销
     * @param queryByIdsRequest 唯一编号列表参数 {@link MarketingQueryByIdsRequest}
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/query-starting-by-ids")
    BaseResponse<MarketingQueryStartingByIdsResponse> queryStartingByIds(@RequestBody @Valid MarketingQueryByIdsRequest queryByIdsRequest);

    /**
     * 将营销活动集合，map成 { goodsId - list<Marketing> } 结构
     * @param request 查询参数 {@link MarketingMapGetByGoodsIdRequest}
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/get-marketing-map-by-goods-id")
    BaseResponse<MarketingMapGetByGoodsIdResponse> getMarketingMapByGoodsId(@RequestBody @Valid MarketingMapGetByGoodsIdRequest request);

    /**
     * 将营销活动集合，map成 { goodsId - list<Marketing> } 结构
     * @param request 查询参数 {@link MarketingMapGetByGoodsIdRequest}
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/get-order-marketing-map")
    BaseResponse<MarketingOrderMapResponse> getOrderMarketingMap(@RequestBody @Valid MarketingMapGetByGoodsIdRequest request);

    /**
     * 查询所有的营销数据
     * @param marketingBaseRequest 分页查询参数 {@link MarketingPageRequest}
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/list")
    BaseResponse<MarketingListResponse> list(@RequestBody @Valid MarketingBaseRequest marketingBaseRequest);

    @PostMapping("/marketing/${application.marketing.version}/groupon/queryGrouponInfoForXsite")
    BaseResponse<MarketingGoodsForXsiteResponse> queryForXsite(@RequestBody @Valid MarketingGoodsForXsiteRequest request);


    /**
     * 会员端根据ids获取营销实体
     * @param request 唯一编号参数 {@link MarketingQueryByIdsRequest}
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/get-by-ids-for-customer")
    BaseResponse<MarketingGetByIdsForCustomerResponse> getByIdsForCustomer(@RequestBody @Valid MarketingQueryByIdsRequest request);

    /**
     * 查询有必选商品的活动
     * @param queryByIdsRequest
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/get-choose-goods-marketing")
    BaseResponse<MarketingQueryByIdsResponse> getChooseGoodsMarketing(@RequestBody @Valid MarketingQueryByIdsRequest queryByIdsRequest);

    /**
     * 查询套装购买商品信息
     * @param queryByIdsRequest
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/get-suit-to-buy-goods")
    BaseResponse<Map<Long, List<MarketingSuitDetialVO>>> getSuitToBuyGoods(@RequestBody @Valid MarketingQueryByIdsRequest queryByIdsRequest);

    /**
     * 根据商品过滤有效套装商品信息
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/get-suit-to-buy-by-goodinfoids")
    BaseResponse<List<MarketingSuitDetialVO>> getSuitToBuyByGoodInfoIds(@RequestBody @Valid MarketingMapGetByGoodsIdRequest request);

    /**
     * 根据活动id查询当前活动是否是套装活动
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/get-suit-by-marketingIds")
    BaseResponse<List<MarketingVO>> getSuitByMarketingIds(@RequestBody @Valid MarketingQueryByIdsRequest queryByIdsRequest);

    /**
     * 通过活动id查询所有套装商品详细信息
     * @param queryByIdsRequest
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/get-suit-detial-by-marketingIds")
    BaseResponse<List<MarketingSuitDetialVO>> getSuitDetialByMarketingIds(@RequestBody @Valid MarketingQueryByIdsRequest queryByIdsRequest);


    /**
     * 参与活动商品goodsInfoids
     * @param request
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/get-marketin-scope")
    BaseResponse<List<String>> getMarketingScope(@RequestBody @Valid AddActivitGoodsRequest request);

    /**
     * 暂停赠品
     * @param request
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/stop-give-goods")
    BaseResponse stopGiveGoods(@RequestBody @Valid MarketingStopGiveGoodsRequest request);

    /**
     * 添加赠品
     * @param request
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/add-activity-give-goods")
    BaseResponse<List<String>> addActivityGiveGoods(@RequestBody @Valid AddActivityGiveGoodsRequest request);

    /**
     * 添加活动商品
     * @param request
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/add-activity-goods")
    BaseResponse<List<String>> addActivityGoods(@RequestBody @Valid AddActivitGoodsRequest request);

    /**
     * 获取活动赠品
     * @param request
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/get-marketing-give-goods")
    BaseResponse<List<String>> getMarketingGiveGoods(@RequestBody @Valid AddActivitGoodsRequest request);


    /**
     * 通过传入商品集合查出商品对应的营销
     * @param request
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/marking-effective-ids")
    BaseResponse<MarketingByGoodsInfoIdAndIdResponse> getEffectiveMarketingByIdsAndGoods(@RequestBody @Valid MarketingEffectiveRequest request);

    /**
     * 查找进行中的促销商家
     */
    @PostMapping("/marketing/${application.marketing.version}/marking-effective-store")
    BaseResponse<MarketingEffectiveStoreResponse>  listEffectiveStore();


    /**
     * 查找进行中的促销商家
     */
    @PostMapping("/marketing/${application.marketing.version}/marking-effective-goodsInfo")
    BaseResponse<List<String>> listEffectiveStoreGoodsInfoIds();
}
