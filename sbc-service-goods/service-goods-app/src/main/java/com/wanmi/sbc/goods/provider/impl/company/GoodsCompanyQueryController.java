package com.wanmi.sbc.goods.provider.impl.company;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.company.GoodsCompanyProvider;
import com.wanmi.sbc.goods.api.provider.company.GoodsCompanyQueryProvider;
import com.wanmi.sbc.goods.api.request.company.*;
import com.wanmi.sbc.goods.api.response.brand.GoodsBrandByIdResponse;
import com.wanmi.sbc.goods.api.response.brand.GoodsBrandListResponse;
import com.wanmi.sbc.goods.api.response.brand.GoodsBrandPageResponse;
import com.wanmi.sbc.goods.api.response.company.*;
import com.wanmi.sbc.goods.bean.vo.GoodsBrandVO;
import com.wanmi.sbc.goods.bean.vo.GoodsCompanyVO;
import com.wanmi.sbc.goods.brand.request.GoodsBrandQueryRequest;
import com.wanmi.sbc.goods.company.model.root.GoodsCompany;
import com.wanmi.sbc.goods.company.request.GoodsCompanyQueryRequest;
import com.wanmi.sbc.goods.company.service.GoodsCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>对品牌查询接口</p>
 */
@RestController
@Validated
public class GoodsCompanyQueryController implements GoodsCompanyQueryProvider {
    @Autowired
    private GoodsCompanyService goodsCompanyService;
    @Override
    public BaseResponse<GoodsCompanyPageResponse> page(@RequestBody @Valid GoodsCompanyPageRequest request) {
        GoodsCompanyQueryRequest queryRequest = new GoodsCompanyQueryRequest();
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        KsBeanUtil.copyPropertiesThird(request, queryRequest);
        return BaseResponse.success(GoodsCompanyPageResponse.builder()
                .goodsCompanyPage(KsBeanUtil.convertPage(goodsCompanyService.page(queryRequest), GoodsCompanyVO.class))
                .build());
    }

    @Override
    public BaseResponse<GoodsCompanyByIdResponse> getById(@RequestBody @Valid GoodsCompanyByIdRequest request) {
        GoodsCompanyByIdResponse response = new GoodsCompanyByIdResponse();
        KsBeanUtil.copyPropertiesThird(goodsCompanyService.findById(request.getCompanyId()), response);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<GoodsCompanyListResponse> list(@Valid GoodsCompanyListRequest request) {
        GoodsCompanyQueryRequest queryRequest = new GoodsCompanyQueryRequest();
        KsBeanUtil.copyPropertiesThird(request, queryRequest);
        List<GoodsCompanyVO> companyVOList = KsBeanUtil.convertList(goodsCompanyService.query(queryRequest), GoodsCompanyVO.class);
        return BaseResponse.success(GoodsCompanyListResponse.builder().goodsCompanyVOList(companyVOList).build());
    }
}
