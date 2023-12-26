package com.wanmi.sbc.goods.api.provider.goods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.common.GoodsCommonBatchUpdateRequest;
import com.wanmi.sbc.goods.api.request.goods.*;
import com.wanmi.sbc.goods.api.response.goods.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * com.wanmi.sbc.goods.api.provider.goods.GoodsProvider
 *
 * @author lipeng
 * @dateTime 2018/11/5 上午9:30
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "GoodsProvider")
public interface GoodsProvider {

    /**
     * 新增商品
     *
     * @param request {@link GoodsAddRequest}
     * @return 新增结果 {@link GoodsAddResponse}
     */
    @PostMapping("/goods/${application.goods.version}/add")
    BaseResponse<GoodsAddResponse> add(@RequestBody @Valid GoodsAddRequest request);

    /**
     * 新增特价商品
     *
     * @param request {@link GoodsAddRequest}
     * @return 新增结果 {@link GoodsAddResponse}
     */
    @PostMapping("/goods/${application.goods.version}/add/special")
    BaseResponse<GoodsAddResponse> addSpecial(@RequestBody @Valid GoodsSpecialRequest request);


    /**
     * 修改特价商品
     *
     * @param request {@link GoodsAddRequest}
     * @return 新增结果 {@link GoodsAddResponse}
     */
    @PostMapping("/goods/${application.goods.version}/modify/special/goods")
    BaseResponse<GoodsAddResponse> modifySpecialGoods(@RequestBody @Valid GoodsSpecialRequest request);

    /**
     * 修改商品
     *
     * @param request {@link GoodsModifyRequest}
     * @return 修改结果 {@link GoodsModifyResponse}
     */
    @PostMapping("/goods/${application.goods.version}/modify")
    BaseResponse<GoodsModifyResponse> modify(@RequestBody @Valid GoodsModifyRequest request);


    /**
     * 修改散批商品
     *
     * @param request {@link GoodsModifyRequest}
     * @return 修改结果 {@link GoodsModifyResponse}
     */
    @PostMapping("/goods/${application.goods.version}/retail-modify")
    BaseResponse<GoodsModifyResponse> modifyRetailGoods(@RequestBody @Valid GoodsModifyRequest request);

    /**
     * 修改散批商品
     *
     * @param request {@link GoodsModifyRequest}
     * @return 修改结果 {@link GoodsModifyResponse}
     */
    @PostMapping("/goods/${application.goods.version}/bulk-modify")
    BaseResponse<GoodsModifyResponse> modifyBulkGoods(@RequestBody @Valid GoodsModifyRequest request);

    /**
     * 新增商品定价
     *
     * @param request {@link GoodsAddPriceRequest}
     */
    @PostMapping("/goods/${application.goods.version}/add-price")
    BaseResponse addPrice(@RequestBody @Valid GoodsAddPriceRequest request);

    /**
     * 新增商品基本信息、基价
     *
     * @param request {@link GoodsAddAllRequest}
     * @return 商品编号 {@link GoodsAddAllResponse}
     */
    @PostMapping("/goods/${application.goods.version}/add-all")
    BaseResponse<GoodsAddAllResponse> addAll(@RequestBody @Valid GoodsAddAllRequest request);

    /**
     * 修改商品基本信息、基价
     *
     * @param request {@link GoodsModifyAllRequest}
     * @return 修改结果 {@link GoodsModifyAllResponse}
     */
    @PostMapping("/goods/${application.goods.version}/modify-all")
    BaseResponse<GoodsModifyAllResponse> modifyAll(@RequestBody @Valid GoodsModifyAllRequest request);

    /**
     * 修改商品基本信息、基价
     *
     * @param request {@link GoodsDeleteByIdsRequest}
     */
    @PostMapping("/goods/${application.goods.version}/delete-by-ids")
    BaseResponse deleteByIds(@RequestBody @Valid GoodsDeleteByIdsRequest request);


    /**
     * 删除零售商品
     *
     * @param request {@link GoodsDeleteByIdsRequest}
     */
    @PostMapping("/goods/${application.goods.version}/delete-retail-by-ids")
    BaseResponse deleteRetailByIds(@RequestBody @Valid GoodsDeleteByIdsRequest request);


    /**
     * 删除散批商品
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/delete-bulk-by-ids")
    BaseResponse deleteBulkByIds(@RequestBody @Valid GoodsDeleteByIdsRequest request);

    /**
     * 修改商品上下架状态
     *
     * @param request {@link GoodsModifyAddedStatusRequest}
     */
    @PostMapping("/goods/${application.goods.version}/modify-added-status")
    BaseResponse modifyAddedStatus(@RequestBody @Valid GoodsModifyAddedStatusRequest request);


    /**
     * 修改商品上下架状态
     *
     * @param request {@link GoodsModifyAddedStatusRequest}
     */
    @PostMapping("/goods/${application.goods.version}/modify-goods_info-status")
    BaseResponse updateAddedFlagByGoodsInfoIds(@RequestBody @Valid GoodsModifyAddedStatusRequest request);

    /**
     * 修改商品分类
     *
     * @param request {@link GoodsModifyCateRequest}
     */
    @PostMapping("/goods/${application.goods.version}/modify-cate")
    BaseResponse modifyCate(@RequestBody @Valid GoodsModifyCateRequest request);

    /**
     * 修改商品商家名称
     *
     * @param request {@link GoodsModifySupplierNameRequest}
     */
    @PostMapping("/goods/${application.goods.version}/modify-supplier-name")
    BaseResponse modifySupplierName(@RequestBody @Valid GoodsModifySupplierNameRequest request);

    /**
     * 修改商品运费模板
     *
     * @param request {@link GoodsModifyFreightTempRequest}
     */
    @PostMapping("/goods/${application.goods.version}/modify-freight-temp")
    BaseResponse modifyFreightTemp(@RequestBody @Valid GoodsModifyFreightTempRequest request);

    /**
     * 商品审核
     *
     * @param request {@link GoodsCheckRequest}
     */
    @PostMapping("/goods/${application.goods.version}/check")
    BaseResponse checkGoods(@RequestBody @Valid GoodsCheckRequest request);

    /**
     * @Author lvzhenwei
     * @Description 更新商品收藏量
     * @Date 16:04 2019/4/11
     * @Param [goodsModifyCollectNumRequest]
     * @return com.wanmi.sbc.common.base.BaseResponse
     **/
    @PostMapping("/goods/${application.goods.version}/update-goods-collect-num")
    BaseResponse updateGoodsCollectNum(@RequestBody @Valid GoodsModifyCollectNumRequest goodsModifyCollectNumRequest);

    /**
     * @Author lvzhenwei
     * @Description 更新商品销量数据
     * @Date 16:08 2019/4/11
     * @Param [goodsModifySalesNumRequest]
     * @return com.wanmi.sbc.common.base.BaseResponse
     **/
    @PostMapping("/goods/${application.goods.version}/update-goods-sales-num")
    BaseResponse updateGoodsSalesNum(@RequestBody @Valid GoodsModifySalesNumRequest goodsModifySalesNumRequest);

    /**
     * @Author lvzhenwei
     * @Description 更新商品评论数量
     * @Date 15:21 2019/4/12
     * @Param [request]
     * @return com.wanmi.sbc.common.base.BaseResponse
     **/
    @PostMapping("/goods/${application.goods.version}/update-goods-favorable-comment-num")
    BaseResponse updateGoodsFavorableCommentNum(@RequestBody @Valid GoodsModifyEvaluateNumRequest request);

    /**
     * 新增spu
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/add_spu")
    BaseResponse<SpecialGoodsAddResponse> addSpu(@RequestBody @Valid SpecialGoodsSaveRequest request);

    /**
     * 修改特价商品
     *
     * @param request {@link GoodsAddRequest}
     * @return 新增结果 {@link GoodsAddResponse}
     */
    @PostMapping("/goods/${application.goods.version}/modify/goods/weightAndCubage")
    BaseResponse weightAndCubage(@RequestBody @Valid GoodsWeightRequest request);


    @PostMapping("/goods/${application.goods.version}/import_goods_modify_batch")
    BaseResponse<GoodsInfoUpdateResponse> batchUpdate(@RequestBody @Valid GoodsCommonBatchUpdateRequest request);

    /**
     * 编辑商品排序
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/modify-goods-seq-num")
    BaseResponse modifyGoodsSeqNum(@RequestBody @Valid GoodsModifySeqNumRequest request);

    /**
     * 批量编辑商品排序
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/batch-modify-goods-seq-num")
    BaseResponse modifyBatchGoodsSeqNum(@RequestBody @Valid GoodsBatchModifySeqNumRequest request);

    /**
     * 批量修改商品类目 (整批)
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/batch-modify-goods-cate")
    BaseResponse batchModifyCate(@RequestBody GoodsBatchModifyCateRequest request);

    /**
     * 批量修改商品类目 (零售)
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/batch-modify-retail-goods-cate")
    BaseResponse batchModifyRetailCate(@RequestBody GoodsBatchModifyCateRequest request);

    /**
     * 批量修改商品类目 (散批)
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/batch-modify-bulk-goods-cate")
    BaseResponse batchModifyBulkCate(@RequestBody GoodsBatchModifyCateRequest request);
    /**
     * 新增商品
     *
     * @param request {@link GoodsAddRequest}
     * @return 新增结果 {@link GoodsAddResponse}
     */
    @PostMapping("/goods/${application.goods.version}/merchant-goods_add")
    BaseResponse<GoodsAddResponse> merchantGoodsAdd(@RequestBody @Valid GoodsAddRequest request);
    @PostMapping("/goods/${application.goods.version}/getSkuInfo-add")
    BaseResponse<GoodsSkuInfoResponse> getSkuInfo(@RequestBody @Valid GoodsGetSkuRequest request);
    @PostMapping("/goods/${application.goods.version}/sysnc-erp")
    BaseResponse  sysnErp(@RequestBody @Valid GoodsAddRequest request);
    @PostMapping("/goods/${application.goods.version}/sysnc-erp-sku")
    BaseResponse  sysnErpSku(@RequestBody @Valid GoodsAddRequest request);


    @PostMapping("/goods/${application.goods.version}/tag/batchGoodsTagRel")
    BaseResponse<Boolean> batchGoodsTagRel(@RequestBody GoodsTagRelReOperateRequest request);
}

