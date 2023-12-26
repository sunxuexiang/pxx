package com.wanmi.sbc.goods.api.response.goodsattribute;

import com.wanmi.sbc.goods.bean.vo.GoodsAttributeVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p> 新增商品属性响应对象</p>
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:22
 * @Description: TODO
 * @Version 1.0
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsAttributeAddResponse implements Serializable {

    private static final long serialVersionUID = 2113304714372754068L;

    @ApiModelProperty(value = "新增商品属性响应对象")
    private GoodsAttributeVo goodsAttributeVo;
}
