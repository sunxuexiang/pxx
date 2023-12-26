package com.wanmi.sbc.goods.bean.dto;

import com.wanmi.sbc.goods.bean.vo.GoodsAttributeVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

/**
 * 商品属性实体关联类
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:25
 * @Description: TODO
 * @Version 1.0
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsAttributeKeyDTO implements Serializable {
    @ApiModelProperty(value = "'ID")
    private String attributeId;
    /**
     * 属性id
     */
    @ApiModelProperty(value = "'属性id")
    private String goodsAttributeId;
    /**
     * 属性 名称
     */
    @ApiModelProperty(value = "'名称")
    private String goodsAttributeValue;
    /**
     * 商品明细id
     */
    @ApiModelProperty(value = "'明细id")
    private String goodsInfoId;
    /**
     * 商品id
     */
    @ApiModelProperty(value = "'商品id")
    private String goodsId;
    /**
     * 名称
     */
    @ApiModelProperty(value = "属性名称")
    private GoodsAttributeVo attribute;
    /**
     * 名称
     */
    @ApiModelProperty(value = "属性名称")
    private String attributeName;

}
