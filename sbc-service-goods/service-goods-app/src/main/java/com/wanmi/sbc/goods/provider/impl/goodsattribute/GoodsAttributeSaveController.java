package com.wanmi.sbc.goods.provider.impl.goodsattribute;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.EnableStatus;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goodsattribute.GoodsAttributeSaveProvider;
import com.wanmi.sbc.goods.api.request.goodsattribute.GoodsAttributeAddRequest;
import com.wanmi.sbc.goods.api.response.goodsattribute.GoodsAttributeAddResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsAttributeVo;
import com.wanmi.sbc.goods.goodsattribute.root.GoodsAttribute;
import com.wanmi.sbc.goods.goodsattribute.service.GoodsAttributeService;
import com.wanmi.sbc.marketing.bean.constant.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;

/**
 * <p> 新增商品属性象</p>
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:22
 * @Description: TODO
 * @Version 1.0
 */
@RestController
@Validated
public class GoodsAttributeSaveController implements GoodsAttributeSaveProvider {

    @Autowired
    private GoodsAttributeService goodsAttributeService ;
    @Override
    public BaseResponse<GoodsAttributeAddResponse> add(@RequestBody @Valid GoodsAttributeAddRequest request) {
        request.setStatus(EnableStatus.ENABLE.toValue());
        request.setDelFlag(DeleteFlag.NO.toValue());
        GoodsAttribute goodsAttribute = new GoodsAttribute();
        KsBeanUtil.copyPropertiesThird(request, goodsAttribute);
        GoodsAttributeVo goodsAttributeVo = goodsAttributeService.wrapperVo(goodsAttributeService.add(goodsAttribute));
        return BaseResponse.success(new GoodsAttributeAddResponse(goodsAttributeVo));
    }

    @Override
    public BaseResponse<GoodsAttributeAddResponse> updateAttribute( @RequestBody @Valid GoodsAttributeAddRequest request) {
        GoodsAttribute goodsAttribute = goodsAttributeService.getById(request.getAttributeId());
        goodsAttribute.setUpdateTime(request.getUpdateTime());
        goodsAttribute.setAttribute(request.getAttribute());
        goodsAttribute.setUpdateTime(LocalDateTime.now());
        goodsAttributeService.updateAttribute(goodsAttribute);
        return BaseResponse.SUCCESSFUL();
    }
    @Override
    public BaseResponse<GoodsAttributeAddResponse> updateStatus( @RequestBody @Valid GoodsAttributeAddRequest request) {
        GoodsAttribute goodsAttribute = goodsAttributeService.getById(request.getAttributeId());
        goodsAttribute.setStatus(request.getStatus());
        goodsAttribute.setUpdateTime(request.getUpdateTime());
        goodsAttribute.setUpdateTime(LocalDateTime.now());
        goodsAttributeService.updateAttribute(goodsAttribute);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<GoodsAttributeAddResponse> deleteById(@RequestBody @Valid GoodsAttributeAddRequest request) {
        GoodsAttribute goodsAttribute = goodsAttributeService.getById(request.getAttributeId());
        goodsAttribute.setUpdateTime(request.getUpdateTime());
        goodsAttribute.setAttribute(request.getAttribute());
        goodsAttribute.setUpdateTime(LocalDateTime.now());
        goodsAttribute.setDelFlag(DeleteFlag.YES);
        goodsAttributeService.updateAttribute(goodsAttribute);
        return BaseResponse.SUCCESSFUL();
    }

}
