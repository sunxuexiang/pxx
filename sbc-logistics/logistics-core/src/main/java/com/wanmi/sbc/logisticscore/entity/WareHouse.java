package com.wanmi.sbc.logisticscore.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>仓库表VO</p>
 */
@Data
public class WareHouse implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * wareId
	 */
	private Long wareId;

	/**
	 * 店铺标识
	 */
	private Long storeId;

	/**
	 * 仓库名称
	 */
	private String wareName;

	/**
	 * 仓库编号
	 */
	private String wareCode;

	/**
	 * 省份
	 */
	private Long provinceId;

	/**
	 * 市
	 */
	private Long cityId;

	/**
	 * 区
	 */
	private Long areaId;

	/**
	 * 详细地址
	 */
	private String addressDetail;

	/**
	 * 是否默认仓 0：否，1：是
	 */
	private Integer defaultFlag;

	/**
	 * 线上仓:0 ,门店仓:1
	 */
	private Integer wareHouseType;


	/**
	 * 是否支持自提 0:否  , 1:是
	 */
	private Integer pickUpFlag;

	/**
	 * 创建人
	 */
	private String createPerson;

	/**
	 * 更新人
	 */
	private String updatePerson;

	/**
	 * 仓库地区id(逗号分隔)
	 */
	private String destinationArea;

	/**
	 * 仓库地区名称(逗号分隔)
	 */
	private String destinationAreaName;

	/**
	 * 已选区域
	 */
	private List<Long> selectedAreas;

    /**
     * 促销仓的id
     */
    private String spPriceId;

	/**
	 * 自身的erpId
	 */
    private String selfErpId;

	/**
	 * 纬度
	 */
	private Double lat;

	/**
	 * 经度
	 */
	private Double lng;

	private Integer delFlag;

    private Integer type;

	private Long distance;

	private Integer stockOutFlag;

}