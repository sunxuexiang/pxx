package com.wanmi.sbc.goods.provider.impl.brand;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.brand.GoodsBrandQueryProvider;
import com.wanmi.sbc.goods.api.request.brand.*;
import com.wanmi.sbc.goods.api.response.brand.*;
import com.wanmi.sbc.goods.bean.vo.GoodsBrandVO;
import com.wanmi.sbc.goods.brand.model.root.GoodsBrand;
import com.wanmi.sbc.goods.brand.request.GoodsBrandQueryRequest;
import com.wanmi.sbc.goods.brand.service.GoodsBrandService;
import com.wanmi.sbc.goods.info.service.GoodsInfoService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

/**
 * <p>对品牌查询接口</p>
 * Created by daiyitian on 2018-11-5-下午6:23.
 */
@RestController
@Validated
public class GoodsBrandQueryController implements GoodsBrandQueryProvider {

    @Autowired
    private GoodsBrandService goodsBrandService;

    @Autowired
    private GoodsInfoService goodsInfoService;

    /**
     * 分页查询品牌列表
     *
     * @param request 品牌查询数据结构 {@link GoodsBrandPageRequest}
     * @return 品牌分页列表 {@link GoodsBrandPageResponse}
     */

    @Override
    public BaseResponse<GoodsBrandPageResponse> page(@RequestBody @Valid GoodsBrandPageRequest request){
        GoodsBrandQueryRequest queryRequest = new GoodsBrandQueryRequest();
        KsBeanUtil.copyPropertiesThird(request, queryRequest);
        return BaseResponse.success(GoodsBrandPageResponse.builder()
                .goodsBrandPage(KsBeanUtil.convertPage(goodsBrandService.page(queryRequest), GoodsBrandVO.class))
                .build());
    }

    /**
     * 条件查询品牌列表
     *
     * @param request 品牌查询数据结构 {@link GoodsBrandListRequest}
     * @return 品牌列表 {@link GoodsBrandListResponse}
     */

    @Override
    public BaseResponse<GoodsBrandListResponse> list(@RequestBody @Valid GoodsBrandListRequest request){
        GoodsBrandQueryRequest queryRequest = new GoodsBrandQueryRequest();
        KsBeanUtil.copyPropertiesThird(request, queryRequest);
        List<GoodsBrandVO> brandVOList = KsBeanUtil.convertList(goodsBrandService.query(queryRequest), GoodsBrandVO.class);
        return BaseResponse.success(GoodsBrandListResponse.builder().goodsBrandVOList(brandVOList).build());
    }

    @Override
    public BaseResponse<GoodsBrandListResponse> listAll(@Valid GoodsBrandListRequest request) {
        GoodsBrandQueryRequest queryRequest = new GoodsBrandQueryRequest();
        KsBeanUtil.copyPropertiesThird(request, queryRequest);
        List<GoodsBrandVO> brandVOList = KsBeanUtil.convertList(goodsBrandService.findByAllBrand(queryRequest), GoodsBrandVO.class);
        return BaseResponse.success(GoodsBrandListResponse.builder().goodsBrandVOList(brandVOList).build());
    }

    /**
     * 根据id查询品牌信息
     *
     * @param request 包含id的查询数据结构 {@link GoodsBrandByIdRequest}
     * @return 品牌信息 {@link GoodsBrandByIdResponse}
     */
    @Override

    public BaseResponse<GoodsBrandByIdResponse> getById(@RequestBody @Valid GoodsBrandByIdRequest request) {
        GoodsBrandByIdResponse response = new GoodsBrandByIdResponse();
        KsBeanUtil.copyPropertiesThird(goodsBrandService.findById(request.getBrandId()), response);
        return BaseResponse.success(response);
    }

    /**
     * 根据ids批量查询品牌列表
     *
     * @param request 包含ids的查询数据结构 {@link GoodsBrandByIdsRequest}
     * @return 品牌列表 {@link GoodsBrandByIdsResponse}
     */
    @Override

    public BaseResponse<GoodsBrandByIdsResponse> listByIds(@RequestBody @Valid GoodsBrandByIdsRequest request){
        List<GoodsBrand> goodsBrandList = goodsBrandService.findByIds(request.getBrandIds());
        if(CollectionUtils.isEmpty(goodsBrandList)){
            return BaseResponse.success(GoodsBrandByIdsResponse.builder().goodsBrandVOList(Collections.emptyList()).build());
        }
        List<GoodsBrandVO> brandVOList = KsBeanUtil.convertList(goodsBrandList, GoodsBrandVO.class);
        return BaseResponse.success(GoodsBrandByIdsResponse.builder().goodsBrandVOList(brandVOList).build());
    }

    /**
     * 根据过滤条件查询品牌列表
     * @param request 过滤条件 {@link GoodsBrandPageRequest}
     * @return
     */
    @Override
    public BaseResponse<GoodsBrandListResponse> getGoodsBrands(@RequestBody @Valid GoodsBrandPageRequest request) {
        GoodsBrandQueryRequest queryRequest = new GoodsBrandQueryRequest();
        KsBeanUtil.copyPropertiesThird(request, queryRequest);
        Page<GoodsBrand> page = goodsBrandService.page(queryRequest);
        List<GoodsBrand> goodsBrandList= page.getContent();
        List<GoodsBrandVO> brandVOList = KsBeanUtil.convertList(goodsBrandList, GoodsBrandVO.class);
        return BaseResponse.success(GoodsBrandListResponse.builder().goodsBrandVOList(brandVOList).build());
    }

    @Override
    public BaseResponse<GoodsBrandByNameResponse> getByName(@Valid GoodsBrandByNameRequest request) {
        GoodsBrand byName = goodsBrandService.getByName(request.getBrandName());
        GoodsBrandVO goodsBrandVO = KsBeanUtil.convert(byName, GoodsBrandVO.class);
        GoodsBrandByNameResponse goodsBrandByNameResponse = new GoodsBrandByNameResponse();
        goodsBrandByNameResponse.setGoodsBrand(goodsBrandVO);
        return BaseResponse.success(goodsBrandByNameResponse);
    }

    @Override
    public BaseResponse<List<Long>> listByClassifyType(Integer type) {
        return BaseResponse.success(goodsInfoService.listByClassifyType(type));
    }
}
