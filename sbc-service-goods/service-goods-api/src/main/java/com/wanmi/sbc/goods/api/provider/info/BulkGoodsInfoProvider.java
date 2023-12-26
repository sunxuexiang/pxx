package com.wanmi.sbc.goods.api.provider.info;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoBatchNosModifyRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoFillGoodsStatusRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoModifyRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoPriceModifyRequest;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoFillGoodsStatusResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoModifyResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoPriceModifyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @Description: 零售商品操作接口
 * @Author: XinJiang
 * @Date: 2022/3/8 15:53
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "BulkGoodsInfoProvider")
public interface BulkGoodsInfoProvider {

    /**
     * 修改商品sku信息
     *
     * @param request 商品sku信息修改结构 {@link GoodsInfoModifyRequest}
     * @return 商品sku信息 {@link GoodsInfoModifyResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/bulk/modify")
    BaseResponse<GoodsInfoModifyResponse> retailModify(@RequestBody @Valid GoodsInfoModifyRequest request);


    /**
     * 修改商品sku设价信息
     *
     * @param request 商品sku设价信息修改结构 {@link GoodsInfoPriceModifyRequest}
     * @return 商品sku设价信息 {@link GoodsInfoPriceModifyResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/retail-bulk-price")
    BaseResponse<GoodsInfoPriceModifyResponse> modifyPrice(@RequestBody @Valid GoodsInfoPriceModifyRequest request);

    /**
     * 根据库存状态/上下架状态/相关店铺状态来填充商品数据的有效性
     *
     * @param request 商品列表数据结构 {@link GoodsInfoFillGoodsStatusRequest}
     * @return 包含商品有效状态的商品列表数据 {@link GoodsInfoFillGoodsStatusResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/fill-bulk-goods-status")
    BaseResponse<GoodsInfoFillGoodsStatusResponse> fillGoodsStatus(@RequestBody @Valid
                                                                           GoodsInfoFillGoodsStatusRequest request);

    /**
     * 批量更新sku的批次号
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/bulk/info/modify-sku-batch-no")
    BaseResponse batchUpdateBatchNos(@RequestBody @Valid GoodsInfoBatchNosModifyRequest request);
}
