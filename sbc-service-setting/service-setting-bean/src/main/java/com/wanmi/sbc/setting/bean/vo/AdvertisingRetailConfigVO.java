package com.wanmi.sbc.setting.bean.vo;

import com.wanmi.sbc.setting.bean.enums.AdvertisingRetailJumpType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 散批广告位配置信息VO实体类
 * @author: XinJiang
 * @time: 2022/4/18 17:41
 */
@ApiModel
@Data
public class AdvertisingRetailConfigVO implements Serializable {

    private static final long serialVersionUID = 8356771046307886664L;

    /**
     * 配置id
     */
    @ApiModelProperty(value = "配置id")
    private String advertisingConfigId;

    /**
     * 散批广告位id
     */
    @ApiModelProperty(value = "散批广告位id")
    private String advertisingId;

    /**
     * 广告位图片地址
     */
    @ApiModelProperty(value = "广告位图片地址")
    private String advertisingImageUrl;

    /**
     * 分栏顶部banner图标
     */
    @ApiModelProperty(value = "分栏-顶部banner图标")
    private String columnsBannerImageUrl;

    /**
     * 跳转类型
     */
    @ApiModelProperty(value = "通栏-跳转类型 0：分类，1：商品")
    private AdvertisingRetailJumpType jumpType;

    /**
     * 跳转编码 分类id/商品erp编码
     */
    @ApiModelProperty(value = "通栏-跳转编码 分类id/商品erp编码")
    private String jumpCode;

    @ApiModelProperty(value = "通栏跳转skuId，如果跳转编码为erp编码 移动端需要填充此字段，便于商品详情接口传入参数查询")
    private String jumpSkuId;

    /**
     * 商品/分类名称
     */
    @ApiModelProperty(value = "通栏-商品/分类名称")
    private String jumpName;

    /**
     * 分栏-商品配置信息列表
     */
    @ApiModelProperty(value = "分栏-商品配置信息列表")
    private List<AdvertisingRetailGoodsConfigVO> advertisingRetailGoodsConfigs;

    /**
     * 分栏-商品skuIds集合，便于前端查询
     */
    @ApiModelProperty(value = "分栏-商品skuIds集合，便于前端查询")
    private List<String> skuIds;

}
