package com.wanmi.sbc.setting.bean.vo;

import com.wanmi.sbc.setting.bean.enums.AdvertisingConfigType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 首页广告位配置VO实体类
 * @author: XinJiang
 * @time: 2022/2/18 10:03
 */
@Data
@ApiModel
public class AdvertisingConfigVO implements Serializable {

    private static final long serialVersionUID = 3423185794183519690L;

    /**
     * 配置id
     */
    @ApiModelProperty(value = "配置id")
    private String advertisingConfigId;

    /**
     * 首页广告位id
     */
    @ApiModelProperty(value = "首页广告位id")
    private String advertisingId;

    /**
     * 广告位图片
     */
    @ApiModelProperty(value = "广告位图片")
    private String advertisingImage;

    /**
     * 跳转链接
     */
    @ApiModelProperty(value = "跳转链接")
    private String jumpLink;

    /**
     * 魔方海报页名称
     */
    @ApiModelProperty(value = "魔方海报页名称")
    private String moFangAdvertisingName;

    /**
     * 魔方海报页编码
     */
    @ApiModelProperty(value = "魔方海报页编码")
    private String moFangPageCode;

    /**
     * 是否套装商品广告页 0否，1是
     */
    @ApiModelProperty(value = "是否套装商品广告页 0:否，1:是")
    private AdvertisingConfigType isSuit;

    /**
     * 定义新入住商家日期类型（1天，2月）
     */
    @ApiModelProperty(value = "定义新入住商家日期类型（1天，2月）")
    private Integer dateType;

    /**
     * 定义新入住商家日期天、月数
     */
    @ApiModelProperty(value = "定义新入住商家日期天、月数")
    private Integer dateNum;

    /**
     * 活动标题
     */
    @ApiModelProperty(value = "活动标题")
    private String activityTitle;

    /**
     * 配置管理广告位主表的Ids通过,号分割
     */
    @ApiModelProperty(value = "配置管理广告位主表的Ids 通过,号分割")
    private String advertisingIds;

    /**
     * 绑定的首页广告位List
     */
    @ApiModelProperty(value = "绑定的首页广告位List")
    private List<AdvertisingVO> advertisingVOList;
}
