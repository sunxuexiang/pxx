package com.wanmi.sbc.goods.provider.impl.brand;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.brand.GoodsBrandProvider;
import com.wanmi.sbc.goods.api.request.brand.*;
import com.wanmi.sbc.goods.api.response.brand.GoodsBrandAddResponse;
import com.wanmi.sbc.goods.api.response.brand.GoodsBrandDeleteResponse;
import com.wanmi.sbc.goods.api.response.brand.GoodsBrandModifyResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsBrandVO;
import com.wanmi.sbc.goods.brand.model.root.GoodsBrand;
import com.wanmi.sbc.goods.brand.request.GoodsBrandQueryRequest;
import com.wanmi.sbc.goods.brand.service.GoodsBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>对品牌操作接口</p>
 * Created by daiyitian on 2018-11-5-下午6:23.
 */
@RestController
@Validated
public class GoodsBrandController implements GoodsBrandProvider {

    @Autowired
    private GoodsBrandService goodsBrandService;

    /**
     * 新增品牌
     *
     * @param request 品牌新增结构 {@link GoodsBrandAddRequest}
     * @return 新增品牌信息 {@link GoodsBrandAddResponse}
     */

    @Override
    public BaseResponse<GoodsBrandAddResponse> add(@RequestBody @Valid GoodsBrandAddRequest request){
        GoodsBrand goodsBrand = goodsBrandService.add(KsBeanUtil.copyPropertiesThird(request, GoodsBrand.class));
        GoodsBrandAddResponse response = new GoodsBrandAddResponse();
        KsBeanUtil.copyPropertiesThird(goodsBrand, response);
        return BaseResponse.success(response);
    }

    /**
     * 修改品牌
     *
     * @param request 品牌修改结构 {@link GoodsBrandModifyRequest}
     * @return 修改品牌信息 {@link GoodsBrandModifyResponse}
     */

    @Override
    public BaseResponse<GoodsBrandModifyResponse> modify(@RequestBody @Valid GoodsBrandModifyRequest request){
        GoodsBrand goodsBrand = goodsBrandService.edit(KsBeanUtil.copyPropertiesThird(request, GoodsBrand.class));
        GoodsBrandModifyResponse response = new GoodsBrandModifyResponse();
        KsBeanUtil.copyPropertiesThird(goodsBrand, response);
        return BaseResponse.success(response);
    }

    /**
     * 根据id删除品牌信息
     *
     * @param request 包含id的删除数据结构 {@link GoodsBrandDeleteByIdRequest}
     * @return 品牌信息 {@link GoodsBrandDeleteResponse}
     */

    @Override
    public BaseResponse<GoodsBrandDeleteResponse> delete(@RequestBody @Valid GoodsBrandDeleteByIdRequest request){
        GoodsBrandDeleteResponse response = new GoodsBrandDeleteResponse();
        KsBeanUtil.copyPropertiesThird(goodsBrandService.delete(request.getBrandId()), response);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<List<GoodsBrandVO>> batchModifySort(@RequestBody @Valid GoodsBrandBatchSortModifyRequest request){
        List<GoodsBrandVO> goodsBrands = request.getGoodsBrandDTOList().stream()
                .map(dto -> KsBeanUtil.convert(goodsBrandService.edit(KsBeanUtil.copyPropertiesThird(dto, GoodsBrand.class)), GoodsBrandVO.class))
                .collect(Collectors.toList());
        return BaseResponse.success(goodsBrands);
    }

    @Override
    public BaseResponse<List<GoodsBrandVO>>  listByCondition(@Valid GoodsBrandListRequest request) {
        GoodsBrandQueryRequest brandRequest = new GoodsBrandQueryRequest();
        KsBeanUtil.copyProperties(request,brandRequest);
        List<GoodsBrand> brandList= goodsBrandService.query(brandRequest);
        List<GoodsBrandVO> resultList = new ArrayList<>();
        for(GoodsBrand goodsBrand : brandList){
            GoodsBrandVO brandVO = new GoodsBrandVO();
            KsBeanUtil.copyProperties(goodsBrand,brandVO);
            resultList.add(brandVO);
        }
        return BaseResponse.success(resultList);

    }
}
