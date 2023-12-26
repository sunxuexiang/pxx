package com.wanmi.sbc.goods.provider.impl.company;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.company.GoodsCompanyProvider;
import com.wanmi.sbc.goods.api.request.company.GoodsCompanyAddRequest;
import com.wanmi.sbc.goods.api.request.company.GoodsCompanyModifyRequest;
import com.wanmi.sbc.goods.api.response.brand.GoodsBrandAddResponse;
import com.wanmi.sbc.goods.api.response.brand.GoodsBrandModifyResponse;
import com.wanmi.sbc.goods.api.response.company.GoodsCompanyAddResponse;
import com.wanmi.sbc.goods.api.response.company.GoodsCompanyModifyResponse;
import com.wanmi.sbc.goods.brand.model.root.GoodsBrand;
import com.wanmi.sbc.goods.company.model.root.GoodsCompany;
import com.wanmi.sbc.goods.company.service.GoodsCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p>对厂商操作接口</p>
 */
@RestController
@Validated
public class GoodsCompanyController implements GoodsCompanyProvider {
    @Autowired
    private GoodsCompanyService goodsCompanyService;

    @Override
    public BaseResponse<GoodsCompanyAddResponse> add(@RequestBody @Valid GoodsCompanyAddRequest request) {
        GoodsCompany goodsCompany= goodsCompanyService.add(KsBeanUtil.copyPropertiesThird(request, GoodsCompany.class));
        GoodsCompanyAddResponse response = new GoodsCompanyAddResponse();
        KsBeanUtil.copyPropertiesThird(goodsCompany, response);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<GoodsCompanyModifyResponse> modify(@RequestBody @Valid GoodsCompanyModifyRequest request) {
        GoodsCompany goodsCompany = goodsCompanyService.edit(KsBeanUtil.copyPropertiesThird(request, GoodsCompany.class));
        GoodsCompanyModifyResponse response = new GoodsCompanyModifyResponse();
        KsBeanUtil.copyPropertiesThird(goodsCompany, response);
        return BaseResponse.success(response);
    }
}
