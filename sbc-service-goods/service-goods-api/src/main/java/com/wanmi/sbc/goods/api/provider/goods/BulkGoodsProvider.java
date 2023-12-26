package com.wanmi.sbc.goods.api.provider.goods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.goods.GoodsBatchModifySeqNumRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsModifyAddedStatusRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsModifyCollectNumRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsModifySeqNumRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "BulkGoodsProvider")
public interface BulkGoodsProvider {

    /**
     * 编辑零售商品排序
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/modify-bulk-goods-seq-num")
    BaseResponse modifyRetailGoodsSeqNum(@RequestBody @Valid GoodsModifySeqNumRequest request);


    /**
     * 修改商品上下架状态
     *
     * @param request {@link GoodsModifyAddedStatusRequest}
     */
    @PostMapping("/goods/${application.goods.version}/modify-added-bulk-status")
    BaseResponse modifyAddedRetailStatus(@RequestBody @Valid GoodsModifyAddedStatusRequest request);

    /**
     * 批量编辑商品排序
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/bulk/batch-modify-goods-seq-num")
    BaseResponse modifyBatchGoodsSeqNum(@RequestBody @Valid GoodsBatchModifySeqNumRequest request);

    @PostMapping("/goods/${application.goods.version}/bulk/update-goods-collect-num")
    BaseResponse updateGoodsCollectNum(@RequestBody @Valid GoodsModifyCollectNumRequest goodsModifyCollectNumRequest);
}
