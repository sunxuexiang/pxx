package com.wanmi.sbc.setting.api.response.baseconfig;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.IconFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * Created by feitingting on 2019/11/8.
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseConfigRopResponse implements Serializable {
    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    private Integer baseConfigId;

    /**
     * PC端商城网址
     */
    @ApiModelProperty(value = "PC端商城网址")
    private String pcWebsite;

    /**
     * 移动端商城网址
     */
    @ApiModelProperty(value = "移动端商城网址")
    private String mobileWebsite;

    /**
     * PC商城logo
     */
    @ApiModelProperty(value = "PC商城logo")
    private String pcLogo;

    /**
     * PC商城登录页banner,最多可添加5个,多个图片间以"|"隔开
     */
    @ApiModelProperty(value = "PC商城登录页banner,最多可添加5个,多个图片间以\"|\"隔开")
    private String pcBanner;

    /**
     * PC商城首页banner,最多可添加5个,多个图片间以"|"隔开
     */
    @ApiModelProperty(value = "PC商城首页banner,最多可添加5个,多个图片间以\"|\"隔开")
    private String pcMainBanner;

    /**
     * 移动商城banner,最多可添加5个,多个图片间以"|"隔开
     */
    @ApiModelProperty(value = "移动商城banner,最多可添加5个,多个图片间以\"|\"隔开")
    private String mobileBanner;

    /**
     * 商城图标，最多添加一个
     */
    @ApiModelProperty(value = "商城图标，最多添加一个")
    private String pcIco;

    /**
     * 商城首页标题
     */
    @ApiModelProperty(value = "商城首页标题")
    private String pcTitle;

    /**
     * 商家网址
     */
    @ApiModelProperty(value = "商家网址")
    private String supplierWebsite;

    /**
     * 会员注册协议
     */
    @ApiModelProperty(value = "会员注册协议")
    private String registerContent;
    /**
     * 全部主体色
     */
    @ApiModelProperty(value = "全部主体色")
    private String allSubjectColor;

    /**
     * 标签按钮色
     */
    @ApiModelProperty(value = "标签按钮色")
    private String tagButtonColor;

    /**
     * 用户协议
     */
    @ApiModelProperty(value = "用户协议")
    private String customerContent;

    /**
     * 隐私协议
     */
    @ApiModelProperty(value = "隐私协议")
    private String privacyContent;

    /**
     * 注销须知
     */
    @ApiModelProperty(value = "注销须知")
    private String cancellationContent;

    /**
     * 图标开关
     */
    @ApiModelProperty(value = "图标开关")
    private IconFlag iconFlag;
}
