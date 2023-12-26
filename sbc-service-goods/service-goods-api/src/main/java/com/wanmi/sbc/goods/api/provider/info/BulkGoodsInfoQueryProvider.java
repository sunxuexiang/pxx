package com.wanmi.sbc.goods.api.provider.info;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoListByParentIdRequest;
import com.wanmi.sbc.goods.api.request.info.*;
import com.wanmi.sbc.goods.api.response.devanninggoodsinfo.DevanningGoodsInfoListResponse;
import com.wanmi.sbc.goods.api.response.info.*;
import com.wanmi.sbc.goods.bean.vo.GoodsImageVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @Description: 散批查询接口
 * @Author: XinJiang
 * @Date: 2022/3/8 15:50
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "BulkGoodsInfoQueryProvider")
public interface BulkGoodsInfoQueryProvider {

    /**
     * 分页查询商品sku视图列表
     *
     * @param request 商品sku视图分页条件查询结构 {@link GoodsInfoViewPageRequest}
     * @return 商品sku视图分页列表 {@link GoodsInfoViewPageResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/bulk-page-view")
    BaseResponse<GoodsInfoViewPageResponse> pageView(@RequestBody @Valid GoodsInfoViewPageRequest request);

    /**
     * 根据商品skuId查询零售商品sku视图
     *
     * @param request 根据商品skuId查询结构 {@link GoodsInfoViewByIdRequest}
     * @return 商品sku视图 {@link GoodsInfoViewByIdResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/get-view-bulk-by-id")
    BaseResponse<GoodsInfoViewByIdResponse> getViewBulkById(@RequestBody @Valid GoodsInfoViewByIdRequest request);

    /**
     * 根据商品skuId批量查询商品sku视图列表
     *
     * @param request 根据批量商品skuId查询结构 {@link GoodsInfoViewByIdsRequest}
     * @return 商品sku视图列表 {@link GoodsInfoViewByIdsResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/bulk-list-view-by-ids")
    BaseResponse<GoodsInfoViewByIdsResponse> listViewByIds(@RequestBody @Valid GoodsInfoViewByIdsRequest request);

    /**
     * 根据商品skuId查询商品sku
     *
     * @param request 根据商品skuId查询结构 {@link GoodsInfoByIdRequest}
     * @return 商品sku {@link GoodsInfoByIdResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/get-bulk-by-id")
    BaseResponse<GoodsInfoByIdResponse> getBulkById(@RequestBody @Valid GoodsInfoByIdRequest request);

    /**
     * 根据商品skuId批量查询商品sku列表
     *
     * @param request 根据批量商品skuId查询结构 {@link GoodsInfoListByIdsRequest}
     * @return 商品sku列表 {@link GoodsInfoListByIdsResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/bulk-list-by-ids")
    BaseResponse<GoodsInfoListByIdsResponse> listByIds(@RequestBody @Valid GoodsInfoListByIdsRequest request);

    /**
     * 分页查询商品sku列表
     *
     * @param request 商品sku分页条件查询结构 {@link GoodsInfoPageRequest}
     * @return 商品sku分页列表 {@link GoodsInfoPageResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/bulk-page")
    BaseResponse<GoodsInfoPageResponse> page(@RequestBody @Valid GoodsInfoPageRequest request);

    /**
     * 根据动态条件查询商品sku列表
     *
     * @param request 根据动态条件查询结构 {@link GoodsInfoListByConditionRequest}
     * @return 商品sku列表 {@link GoodsInfoListByConditionResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/bulk-list-by-condition")
    BaseResponse<GoodsInfoListByConditionResponse> listByCondition(@RequestBody @Valid GoodsInfoListByConditionRequest
                                                                           request);
    /**
     * 根据动态条件统计商品sku个数
     *
     * @param request 根据动态条件统计结构 {@link GoodsInfoCountByConditionRequest}
     * @return 商品sku个数 {@link GoodsInfoCountByConditionResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/bulk-count-by-condition")
    BaseResponse<GoodsInfoCountByConditionResponse> countByCondition(@RequestBody @Valid
                                                                             GoodsInfoCountByConditionRequest
                                                                             request);

    /**
     * 根据商品skuId批量查询商品sku列表
     *
     * @param request 根据批量商品skuId查询结构 {@link GoodsInfoListByIdsRequest}
     * @return 商品sku列表 {@link GoodsInfoListByIdsResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/bulk-list-goodsinfo-stock-by-ids")
    BaseResponse<GoodsInfoListByIdsResponse> listGoodsInfoAndStcokByIds(@RequestBody @Valid GoodsInfoAndStockListByIdsRequest request);

    /**
     * 根据商品skuId批量查询商品sku列表
     *
     * @param request 根据批量商品skuId查询结构 {@link GoodsInfoListByIdsRequest}
     * @return 商品sku列表 {@link GoodsInfoListByIdsResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/bulk-list-goodsinfoid-by-ids")
    BaseResponse<GiftGoodsInfoListByIdsResponse> findGoodsInfoByIds(@RequestBody @Valid GoodsInfoListByIdsRequest request);

    @PostMapping("/goods/${application.goods.version}/info/bulk-list-view-by-ids-matchFlag")
    BaseResponse<GoodsInfoViewByIdsResponse> listViewByIdsByMatchFlag(@RequestBody @Valid GoodsInfoViewByIdsRequest request);

    @PostMapping("/goods/${application.goods.version}/info/bulk-list-view-by-ids-matchFlag-no-stock")
    BaseResponse<GoodsInfoViewByIdsResponse> listViewByIdsByMatchFlagNoStock(@RequestBody @Valid GoodsInfoViewByIdsRequest request);

    /**
     * 获取商品retail goodsInfoImg为空的列表
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/info/bulk-list-view-by-goodsInfoImg")
    BaseResponse<GoodsInfoViewByIdsResponse> listViewByGoodsInfoImgIsNull();

    /**
     * 通过spuIds获取商品图片map集合
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/info/bulk-goods-images")
    BaseResponse<Map<String, List<GoodsImageVO>>> getGoodsImageOfMap(@RequestBody @Valid GoodsImagesBySpuIdsRequest request);

    /**
     * 修改商品图片地址
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/info/bulk-update-images")
    BaseResponse updateGoodsImg(@RequestBody @Valid GoodsInfoUpdateImgRequest request);

    /**
     * 通过erp编码获取商品信息
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/info/bulk-get-by-erpNo")
    BaseResponse<GoodsInfoViewByIdResponse> getGoodsInfoByErpNo(@RequestBody @Valid GoodsInfoByErpNoRequest request);

    /**
     * 根据商品sku父级Id查询商品
     *
     */
    @PostMapping("/goods/${application.goods.version}/devanning/bulk-list-by-parent-id")
    BaseResponse<DevanningGoodsInfoListResponse> listByParentId(@RequestBody @Valid DevanningGoodsInfoListByParentIdRequest request);

    /**
     * 通过spu查询sku
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/info/bulk/get-by-goodsid")
    BaseResponse<GoodsInfoByGoodsIdresponse> getByGoodsId(@RequestBody DistributionGoodsChangeRequest request);
}
