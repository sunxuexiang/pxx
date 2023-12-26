package com.wanmi.sbc.goods.api.provider.common;

import com.wanmi.sbc.common.base.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author: wanggang
 * @createDate: 2018/11/2 9:52
 * @version: 1.0
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "GoodsCommonQueryProvider")
public interface GoodsCommonQueryProvider {

    /**
     * 递归方式，获取全局唯一SPU编码
     * @return SPU编码
     */
    @PostMapping("/goods/${application.goods.version}/common/get-spu-no-by-unique")
    BaseResponse<String> getSpuNoByUnique();

    /**
     * 获取Spu编码
     * @return SPU编码
     */
    @PostMapping("/goods/${application.goods.version}/common/get-spu-no")
    BaseResponse<String> getSpuNo();

    /**
     * 递归方式，获取全局唯一SKU编码
     * @return SKU编码
     */
    @PostMapping("/goods/${application.goods.version}/common/get-sku-no-by-unique")
    BaseResponse<String> getSkuNoByUnique();

    /**
     * 获取Sku编码
     * @return Sku编码
     */
    @PostMapping("/goods/${application.goods.version}/common/get-sku-no")
    BaseResponse<String> getSkuNo();
}
