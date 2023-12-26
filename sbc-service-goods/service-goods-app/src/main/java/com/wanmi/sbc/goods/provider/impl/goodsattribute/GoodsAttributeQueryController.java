package com.wanmi.sbc.goods.provider.impl.goodsattribute;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goodsattribute.GoodsAttributeQueryProvider;
import com.wanmi.sbc.goods.api.request.company.GoodsCompanyByIdRequest;
import com.wanmi.sbc.goods.api.request.company.GoodsCompanyListRequest;
import com.wanmi.sbc.goods.api.request.goodsattribute.GoodsAttributeAddRequest;
import com.wanmi.sbc.goods.api.request.goodsattribute.GoodsAttributeQueryRequest;
import com.wanmi.sbc.goods.api.response.company.GoodsCompanyByIdResponse;
import com.wanmi.sbc.goods.api.response.company.GoodsCompanyListResponse;
import com.wanmi.sbc.goods.api.response.company.GoodsCompanyPageResponse;
import com.wanmi.sbc.goods.api.response.goods.GoodsPageResponse;
import com.wanmi.sbc.goods.api.response.goodsattribute.GoodsAttributeAddResponse;
import com.wanmi.sbc.goods.api.response.goodsattribute.GoodsAttributeListResponse;
import com.wanmi.sbc.goods.api.response.goodsattribute.GoodsAttributePageResponse;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.goods.company.request.GoodsCompanyQueryRequest;
import com.wanmi.sbc.goods.goodsattribute.reponse.GoodsAttrQueryResponse;
import com.wanmi.sbc.goods.goodsattribute.request.GoodsAtrrQueryRequest;
import com.wanmi.sbc.goods.goodsattribute.service.GoodsAttributeService;
import com.wanmi.sbc.goods.info.model.root.Goods;
import com.wanmi.sbc.goods.info.reponse.GoodsQueryResponse;
import com.wanmi.sbc.goods.info.request.GoodsQueryRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * <p> 查询商品属性象</p>
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:22
 * @Description: TODO
 * @Version 1.0
 */
@RestController
@Validated
public class GoodsAttributeQueryController implements GoodsAttributeQueryProvider {

    @Autowired
    private GoodsAttributeService goodsAttributeService ;
    @Override
    public BaseResponse<GoodsAttributePageResponse> page(GoodsAttributeQueryRequest request) {
        GoodsAtrrQueryRequest queryRequest = new GoodsAtrrQueryRequest();
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        KsBeanUtil.copyPropertiesThird(request, queryRequest);
        return BaseResponse.success(GoodsAttributePageResponse.builder()
                .attributeVos(KsBeanUtil.convertPage(goodsAttributeService.page(queryRequest), GoodsAttributeVo.class))
                .build());

    }
    @Override
    public BaseResponse<GoodsAttributeListResponse> getList(GoodsAttributeQueryRequest request) {
        GoodsAtrrQueryRequest queryRequest = new GoodsAtrrQueryRequest();
        KsBeanUtil.copyPropertiesThird(request, queryRequest);
        List<GoodsAttributeVo> companyVOList = KsBeanUtil.convertList(goodsAttributeService.query(queryRequest), GoodsAttributeVo.class);
        return BaseResponse.success(GoodsAttributeListResponse.builder().attributeVos(companyVOList).build());
    }

    @Override
    public BaseResponse<GoodsAttributeAddResponse> findByGoodsAttributeId(GoodsAttributeAddRequest request) {
        GoodsAttributeAddResponse response = new GoodsAttributeAddResponse();
        KsBeanUtil.copyPropertiesThird(goodsAttributeService.findById(request.getAttributeId()), response);
        return BaseResponse.success(response);
    }

}
