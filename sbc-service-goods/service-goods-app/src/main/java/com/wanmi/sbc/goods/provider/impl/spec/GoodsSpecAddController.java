package com.wanmi.sbc.goods.provider.impl.spec;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.spec.GoodsSpecProvider;
import com.wanmi.sbc.goods.api.request.spec.GoodsSpecAddRequest;
import com.wanmi.sbc.goods.api.request.spec.GoodsSpecListByGoodsIdsRequest;
import com.wanmi.sbc.goods.api.response.spec.GoodsInfoSpecAddResponse;
import com.wanmi.sbc.goods.api.response.spec.GoodsSpecListByGoodsIdsResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsSpecVO;
import com.wanmi.sbc.goods.spec.service.GoodsSpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 商品规格添加服务
 *
 * @author chenjun
 * @dateTime 2020/6/1 19:57
 */
@RestController
@Validated
public class GoodsSpecAddController implements GoodsSpecProvider {

    @Autowired
    private GoodsSpecService goodsSpecService;


    @Override
    public BaseResponse<GoodsInfoSpecAddResponse> addSpec(@Valid GoodsSpecAddRequest request) {
        GoodsInfoSpecAddResponse goodsInfoSpecAddResponse = new GoodsInfoSpecAddResponse();
        goodsInfoSpecAddResponse.setGoodsSpecVOS(KsBeanUtil
                .convertList(goodsSpecService.AddSpec(request),GoodsSpecVO.class));
         return BaseResponse.success(goodsInfoSpecAddResponse);
    }
}
