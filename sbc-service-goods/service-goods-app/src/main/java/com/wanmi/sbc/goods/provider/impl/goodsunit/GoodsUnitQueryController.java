package com.wanmi.sbc.goods.provider.impl.goodsunit;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goodsunit.GoodsUnitQueryProvider;
import com.wanmi.sbc.goods.api.request.goodsunit.StoreGoodsUnitAddRequest;
import com.wanmi.sbc.goods.api.request.goodsunit.StoreGoodsUnitQueryRequest;
import com.wanmi.sbc.goods.api.response.goodsunit.GoodsUnitAddResponse;
import com.wanmi.sbc.goods.api.response.goodsunit.GoodsUnitListResponse;
import com.wanmi.sbc.goods.api.response.goodsunit.GoodsUnitPageResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsUnitVo;
import com.wanmi.sbc.goods.goodsunit.request.GoodsUnitQueryRequest;
import com.wanmi.sbc.goods.goodsunit.service.GoodsUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p> 查询商品属性象</p>
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:22
 * @Description: TODO
 * @Version 1.0
 */
@RestController
@Validated
public class GoodsUnitQueryController implements GoodsUnitQueryProvider {

    @Autowired
    private GoodsUnitService goodsUnitService ;
    @Override
    public BaseResponse<GoodsUnitPageResponse> page(StoreGoodsUnitQueryRequest request) {
        GoodsUnitQueryRequest queryRequest = new GoodsUnitQueryRequest();
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        KsBeanUtil.copyPropertiesThird(request, queryRequest);
        return BaseResponse.success(GoodsUnitPageResponse.builder()
                .goodsUnitVos(KsBeanUtil.convertPage(goodsUnitService.page(queryRequest), GoodsUnitVo.class))
                .build());

    }
    @Override
    public BaseResponse<GoodsUnitListResponse> getList(StoreGoodsUnitQueryRequest request) {
        GoodsUnitQueryRequest queryRequest = new GoodsUnitQueryRequest();
        KsBeanUtil.copyPropertiesThird(request, queryRequest);
        List<GoodsUnitVo> companyVOList = KsBeanUtil.convertList(goodsUnitService.query(queryRequest), GoodsUnitVo.class);
        return BaseResponse.success(GoodsUnitListResponse.builder().goodsUnitVos(companyVOList).build());
    }

    @Override
    public BaseResponse<GoodsUnitAddResponse> findByStoreGoodsUnitId(StoreGoodsUnitAddRequest request) {
        GoodsUnitAddResponse response = new GoodsUnitAddResponse();
        KsBeanUtil.copyPropertiesThird(goodsUnitService.findById(request.getStoreGoodsUnitId()), response);
        return BaseResponse.success(response);
    }

}
