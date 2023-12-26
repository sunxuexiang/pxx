package com.wanmi.sbc.goods.provider.impl.excel;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.provider.excel.GoodsExcelProvider;
import com.wanmi.sbc.goods.api.response.brand.GoodsBrandSortExcelResponse;
import com.wanmi.sbc.goods.api.response.excel.GoodsExcelExportTemplateResponse;
import com.wanmi.sbc.goods.api.response.goods.GoodsSortExcelResponse;
import com.wanmi.sbc.goods.info.service.GoodsExcelService;
import com.wanmi.sbc.goods.info.service.GoodsSortExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * com.wanmi.sbc.goods.provider.impl.excel.GoodsExcelController
 *
 * @author lipeng
 * @dateTime 2018/11/7 下午3:19
 */
@RestController
@Validated
public class GoodsExcelController implements GoodsExcelProvider {

    @Autowired
    private GoodsExcelService goodsExcelService;

    @Autowired
    private GoodsSortExcelService goodsSortExcelService;

    /**
     * 获取商品excel模板
     * @return base64位文件流字符串 {@link GoodsExcelExportTemplateResponse}
     */
    @Override
    public BaseResponse<GoodsExcelExportTemplateResponse> exportTemplate() {
        return BaseResponse.success(GoodsExcelExportTemplateResponse.builder()
                .file(goodsExcelService.exportTemplate()).build());
    }

    @Override
    public BaseResponse<GoodsSortExcelResponse> exportSortTemplate() {
        return BaseResponse.success(GoodsSortExcelResponse.builder()
                .file(goodsSortExcelService.exportTemlate())
                .build());
    }
}
