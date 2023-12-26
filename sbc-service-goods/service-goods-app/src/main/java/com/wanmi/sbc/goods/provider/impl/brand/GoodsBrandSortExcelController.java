package com.wanmi.sbc.goods.provider.impl.brand;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.provider.brand.GoodsBrandExcelProvider;
import com.wanmi.sbc.goods.api.response.brand.GoodsBrandSortExcelResponse;
import com.wanmi.sbc.goods.brand.service.GoodsBrandSortExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yang
 * @since 2020/12/31
 */
@Validated
@RestController
public class GoodsBrandSortExcelController implements GoodsBrandExcelProvider {

    @Autowired
    private GoodsBrandSortExcelService goodsBrandSortExcelService;

    @Override
    public BaseResponse<GoodsBrandSortExcelResponse> exportTemplate() {
        return BaseResponse.success(GoodsBrandSortExcelResponse.builder()
                .file(goodsBrandSortExcelService.exportTemlate())
                .build());
    }
}
