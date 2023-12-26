package com.wanmi.sbc.marketing.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class GoodsMarketingExportVo {

    @ApiModelProperty(value = "ERP编码")
    private String erpGoodsInfoNo;

    @ApiModelProperty(value = "SKU编码")
    private String goodsInfoNo;

    @ApiModelProperty(value = "商品名称")
    private String goodsInfoName;

    @ApiModelProperty(value = "品牌名称")
    private String brandName;

    @ApiModelProperty(value = "商品分类")
    private String cateName;

    @ApiModelProperty(value = "活动类型")
    private String subType;

    @ApiModelProperty(value = "活动名称")
    private String marketingName;

    @ApiModelProperty(value = "活动时间")
    private String beginAndEndTime;

    @ApiModelProperty(value = "活动力度")
    private String suitCouponDesc;

    @ApiModelProperty(value = "适用区域")
    private String wareName;

    @ApiModelProperty(value = "是否叠加")
    private String isOverlap;

    @ApiModelProperty(value = "是否终止")
    private String terminationFlag;

    @ApiModelProperty(value = "最后操作人")
    private String updatePerson;

    @ApiModelProperty(value = "最后操作时间")
    private String updateTime;
    
    /**
     * 活动状态
     */
    @ApiModelProperty(value = "活动状态")
    private String activityStatus;
}
