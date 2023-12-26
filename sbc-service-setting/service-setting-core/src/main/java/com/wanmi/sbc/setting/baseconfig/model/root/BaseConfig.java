package com.wanmi.sbc.setting.baseconfig.model.root;


import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.IconFlag;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * <p>基本设置实体类</p>
 * @author lq
 * @date 2019-11-05 16:08:31
 */
@Data
@Entity
@Table(name = "base_config")
public class BaseConfig implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 基本设置ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "base_config_id")
	private Integer baseConfigId;

	/**
	 * PC端商城网址
	 */
	@Column(name = "pc_website")
	private String pcWebsite;

	/**
	 * 移动端商城网址
	 */
	@Column(name = "mobile_website")
	private String mobileWebsite;

	/**
	 * PC商城logo
	 */
	@Column(name = "pc_logo")
	private String pcLogo;

	/**
	 * PC商城banner,最多可添加5个,多个图片间以'|'隔开
	 */
	@Column(name = "pc_banner")
	private String pcBanner;

	/**
	 * 移动商城banner,最多可添加5个,多个图片间以'|'隔开
	 */
	@Column(name = "mobile_banner")
	private String mobileBanner;

	/**
	 * PC商城首页banner,最多可添加5个,多个图片间以'|'隔开
	 */
	@Column(name = "pc_main_banner")
	private String pcMainBanner;

	/**
	 * 网页ico
	 */
	@Column(name = "pc_ico")
	private String pcIco;

	/**
	 * pc商城标题
	 */
	@Column(name = "pc_title")
	private String pcTitle;

	/**
	 * 商家后台登录网址
	 */
	@Column(name = "supplier_website")
	private String supplierWebsite;

	/**
	 * 会员注册协议
	 */
	@Column(name = "register_content")
	private String registerContent;

	/**
	 * 全部主体色
	 */
	@Column(name = "all_subject_color")
	private String allSubjectColor;

	/**
	 * 标签按钮色
	 */
	@Column(name = "tag_button_color")
	private String tagButtonColor;

	/**
	 * 用户协议
	 */
	@Column(name = "customer_content")
	private String customerContent;

	/**
	 * 隐私协议
	 */
	@Column(name = "privacy_content")
	private String privacyContent;

	/**
	 * 注销须知
	 */
	@Column(name = "cancellation_content")
	private String cancellationContent;

	/**
	 * 图标类型
	 * 0-默认，1-囤货，2-年货
	 */
	@Column(name = "icon_flag")
	private IconFlag iconFlag;

}