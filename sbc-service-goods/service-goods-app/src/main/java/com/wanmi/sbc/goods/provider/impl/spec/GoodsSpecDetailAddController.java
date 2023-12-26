package com.wanmi.sbc.goods.provider.impl.spec;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.spec.GoodsSpecDetailProvider;
import com.wanmi.sbc.goods.api.request.spec.GoodsSpecDetailAddRequest;
import com.wanmi.sbc.goods.api.response.spec.GoodsInfoSpecDetailAddResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsSpecDetailVO;
import com.wanmi.sbc.goods.spec.model.root.GoodsSpecDetail;
import com.wanmi.sbc.goods.spec.service.GoodsSpecDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 商品规格值添加服务
 *
 * @author chenjun
 * @dateTime 2020/6/2 13:45
 */
@RestController
@Validated
public class GoodsSpecDetailAddController implements GoodsSpecDetailProvider {
    @Autowired
    private GoodsSpecDetailService goodsSpecDetailService;

    @Override
    public BaseResponse<GoodsInfoSpecDetailAddResponse> addSpecDetail(@Valid GoodsSpecDetailAddRequest request) {
        GoodsInfoSpecDetailAddResponse goodsInfoSpecDetailAddResponse = new GoodsInfoSpecDetailAddResponse();
        List<GoodsSpecDetail> goodsSpecDetails = goodsSpecDetailService.AddSpecDetail(request);
        goodsInfoSpecDetailAddResponse.setGoodsSpecDetailVOS(KsBeanUtil
                .convertList(goodsSpecDetails, GoodsSpecDetailVO.class));
        return BaseResponse.success(goodsInfoSpecDetailAddResponse);
    }
}
