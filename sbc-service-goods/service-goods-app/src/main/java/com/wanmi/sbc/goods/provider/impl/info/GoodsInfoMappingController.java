package com.wanmi.sbc.goods.provider.impl.info;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoMappingProvider;
import com.wanmi.sbc.goods.api.request.info.CatWareGetGoodsInfoMappingRequest;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoMappingVO;
import com.wanmi.sbc.goods.mapping.model.root.GoodsInfoMapping;
import com.wanmi.sbc.goods.mapping.service.GoodsInfoMappingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@Validated
public class GoodsInfoMappingController implements GoodsInfoMappingProvider {

    @Autowired
    private GoodsInfoMappingService goodsInfoMappingService;

    @Override
    public BaseResponse<List<GoodsInfoMappingVO>> catWareGetGoodsInfoMapping(@Valid CatWareGetGoodsInfoMappingRequest request) {
        List<GoodsInfoMapping> goodsInfoMappings = goodsInfoMappingService.catWareGetGoodsInfoMapping(CatWareGetGoodsInfoMappingRequest.builder().goodsInfoIds(request.getGoodsInfoIds()).wareId(request.getWareId()).build());
        List<GoodsInfoMappingVO> responseList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(goodsInfoMappings)){
            goodsInfoMappings.forEach(var->{
                GoodsInfoMappingVO vo = new GoodsInfoMappingVO();
                BeanUtils.copyProperties(var,vo);
                responseList.add(vo);
            });
        }
        return BaseResponse.success(responseList);
    }
}
