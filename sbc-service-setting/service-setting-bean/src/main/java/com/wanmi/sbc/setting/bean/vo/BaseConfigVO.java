package com.wanmi.sbc.setting.bean.vo;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.IconFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>基本设置VO</p>
 * @author lq
 * @date 2019-11-05 16:08:31
 */
@ApiModel
@Data
public class BaseConfigVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 基本设置ID
	 */
	@ApiModelProperty(value = "基本设置ID")
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
	 * PC商城banner,最多可添加5个,多个图片间以'|'隔开
	 */
	@ApiModelProperty(value = "PC商城banner,最多可添加5个,多个图片间以'|'隔开")
	private String pcBanner;

	/**
	 * 移动商城banner,最多可添加5个,多个图片间以'|'隔开
	 */
	@ApiModelProperty(value = "移动商城banner,最多可添加5个,多个图片间以'|'隔开")
	private String mobileBanner;

	/**
	 * PC商城首页banner,最多可添加5个,多个图片间以'|'隔开
	 */
	@ApiModelProperty(value = "PC商城首页banner,最多可添加5个,多个图片间以'|'隔开")
	private String pcMainBanner;

	/**
	 * 网页ico
	 */
	@ApiModelProperty(value = "网页ico")
	private String pcIco;

	/**
	 * pc商城标题
	 */
	@ApiModelProperty(value = "pc商城标题")
	private String pcTitle;

	/**
	 * 商家后台登录网址
	 */
	@ApiModelProperty(value = "商家后台登录网址")
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
	 * 图标开关
	 */
	@ApiModelProperty(value = "图标开关")
	private IconFlag iconFlag;

}