package com.wanmi.sbc.goods.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 散批鲸喜推荐VO实体类
 * @author: XinJiang
 * @time: 2022/4/20 9:36
 */
@ApiModel
@Data
public class RetailGoodsRecommendSettingVO implements Serializable {

    private static final long serialVersionUID = -3174519570668039731L;

    /**
     * 推荐主键id
     */
    @ApiModelProperty(value = "推荐主键id")
    private String recommendId;

    /**
     * 商品skuId
     */
    @ApiModelProperty(value = "商品skuId")
    private String goodsInfoId;

    /**
     * 排序顺序
     */
    @ApiModelProperty(value = "排序顺序")
    private Integer sortNum;

}
