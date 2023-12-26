package com.wanmi.sbc.goods.api.provider.brand;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.response.brand.GoodsBrandSortExcelResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * <p>品牌导入操作接口</p>
 *
 * @author yang
 * @since 2019/5/22
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "GoodsBrandExcelProvider")
public interface GoodsBrandExcelProvider {

    /**
     * 导出品牌排序模板
     *
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/brand/sort/export-template")
    BaseResponse<GoodsBrandSortExcelResponse> exportTemplate();
}
