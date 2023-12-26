package com.wanmi.sbc.goods.positionpicture.model.root;

import com.wanmi.sbc.common.enums.DeleteFlag;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "position_picture")
public class PositionPicture implements Serializable {
	private static final long serialVersionUID = 1L;


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "position_id")
	private Long positionId;


	/**
	 * 仓库id
	 */
	@Column(name = "ware_id")
	private Long wareId;

	/**
	 * 图片地址
	 */
	@Column(name = "image_url")
	private String imageUrl;


	@Column(name = "del_flag")
	private int delFlag;

	/**
	 * 类型0是批发1是散批2是零售
	 */
	@Column(name = "type")
	private int type;

	/**
	 * 品牌店铺ID
	 */
	@Column(name = "store_id")
	private Long storeId;

	/**
	 * 服务电话
	 */
	@Column(name = "service_phone")
	private String servicePhone;


	/**
	 * 客服工作时间
	 */
	@Column(name = "service_time")
	private String serviceTime;

	/**
	 * 客服工作时间开关：0、关闭；1、开启
	 */
	@Column(name = "service_time_switch")
	private Integer serviceTimeSwitch;


	/**
	 * 关于商品
	 */
	@Column(name = "about_product")
	private String aboutProduct;

	/**
	 * 关于商品开关：0、关闭；1、开启
	 */
	@Column(name = "about_product_switch")
	private Integer aboutProductSwitch;


	/**
	 * 关于物流
	 */
	@Column(name = "about_logistics")
	private String aboutLogistics;

	/**
	 * 关于物流开关：0、关闭；1、开启
	 */
	@Column(name = "about_logistics_switch")
	private Integer aboutLogisticsSwitch;


	/**
	 * 关于售后
	 */
	@Column(name = "about_sales")
	private String aboutSales;

	/**
	 * 关于售后开关：0、关闭；1、开启
	 */
	@Column(name = "about_sales_switch")
	private Integer aboutSalesSwitch;

	/**
	 * 背景颜色
	 */
	@Column(name = "background_color")
	private String backgroundColor;

	/**
	 * 描述开关：0、关闭；1、开启
	 */
	@Column(name = "desc_switch")
	private Integer descSwitch;

}