package com.wanmi.sbc.setting.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 散批广告位分栏商品配置信VO实体类
 * @author: XinJiang
 * @time: 2022/4/20 15:48
 */
@ApiModel
@Data
public class AdvertisingRetailGoodsConfigVO implements Serializable {

    private static final long serialVersionUID = 6905237047824106256L;

    /**
     * 主键id
     */
    @ApiModelProperty("主键id")
    private String id;

    /**
     * 散批广告位id
     */
    @ApiModelProperty("散批广告位id")
    private String advertisingConfigId;

    /**
     * 商品skuId
     */
    @ApiModelProperty("商品skuId")
    private String goodsInfoId;

    /**
     * 排序顺序
     */
    @ApiModelProperty("排序顺序")
    private Integer sortNum;

}
