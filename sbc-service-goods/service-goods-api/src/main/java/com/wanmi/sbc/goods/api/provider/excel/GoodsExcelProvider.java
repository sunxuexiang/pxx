package com.wanmi.sbc.goods.api.provider.excel;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.response.brand.GoodsBrandSortExcelResponse;
import com.wanmi.sbc.goods.api.response.excel.GoodsExcelExportTemplateResponse;
import com.wanmi.sbc.goods.api.response.goods.GoodsSortExcelResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * com.wanmi.sbc.goods.api.provider.goodsexcel.GoodsExcelProvider
 * 商品excel操作接口，对应改造之前的GoodsExcelService
 * @author lipeng
 * @dateTime 2018/11/6 上午11:14
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "GoodsExcelProvider")
public interface GoodsExcelProvider {

    /**
     * 获取商品excel模板
     * @return base64位文件流字符串 {@link GoodsExcelExportTemplateResponse}
     */
    @PostMapping("/goods/${application.goods.version}/excel/export-template")
    BaseResponse<GoodsExcelExportTemplateResponse> exportTemplate();

    /**
     * 导出商品排序模板
     *
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/sort/export-template")
    BaseResponse<GoodsSortExcelResponse> exportSortTemplate();
}
