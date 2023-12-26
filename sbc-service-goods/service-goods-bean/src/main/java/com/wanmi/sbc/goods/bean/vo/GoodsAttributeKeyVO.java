package com.wanmi.sbc.goods.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
/**
 * @Author shiGuangYi
 * @createDate 2023-06-21 14:58
 * @Description: TODO
 * @Version 1.0
 */
@ApiModel
@Data
public class GoodsAttributeKeyVO implements Serializable {
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
    /**
     * 名称
     */
    @ApiModelProperty(value = "属性描述")
    private String attributeNameDesc;
}
