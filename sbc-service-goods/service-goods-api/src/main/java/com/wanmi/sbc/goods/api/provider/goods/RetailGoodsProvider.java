package com.wanmi.sbc.goods.api.provider.goods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.goods.*;
import com.wanmi.sbc.goods.api.response.goods.GoodsPageResponse;
import com.wanmi.sbc.goods.api.response.goods.GoodsViewByIdResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "RetailGoodsProvider")
public interface RetailGoodsProvider {

    /**
     * 编辑零售商品排序
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/modify-retail-goods-seq-num")
    BaseResponse modifyRetailGoodsSeqNum(@RequestBody @Valid GoodsModifySeqNumRequest request);


    /**
     * 修改商品上下架状态
     *
     * @param request {@link GoodsModifyAddedStatusRequest}
     */
    @PostMapping("/goods/${application.goods.version}/modify-added-retail-status")
    BaseResponse modifyAddedRetailStatus(@RequestBody @Valid GoodsModifyAddedStatusRequest request);

    /**
     * 批量编辑商品排序
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/retail/batch-modify-goods-seq-num")
    BaseResponse modifyBatchGoodsSeqNum(@RequestBody @Valid GoodsBatchModifySeqNumRequest request);
}
