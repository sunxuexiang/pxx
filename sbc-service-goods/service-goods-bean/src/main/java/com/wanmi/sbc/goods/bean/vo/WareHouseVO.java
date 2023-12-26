package com.wanmi.sbc.goods.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.PickUpFlag;
import com.wanmi.sbc.common.enums.WareHouseType;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>仓库表VO</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:21:37
 */
@ApiModel
@Data
public class WareHouseVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * wareId
	 */
	@ApiModelProperty(value = "wareId")
	private Long wareId;

	/**
	 * 店铺标识
	 */
	@ApiModelProperty(value = "店铺标识")
	private Long storeId;

	/**
	 * 仓库名称
	 */
	@ApiModelProperty(value = "仓库名称")
	private String wareName;

	/**
	 * 仓库编号
	 */
	@ApiModelProperty(value = "仓库编号")
	private String wareCode;

	/**
	 * 省份
	 */
	@ApiModelProperty(value = "省份")
	private Long provinceId;

	/**
	 * 市
	 */
	@ApiModelProperty(value = "市")
	private Long cityId;

	/**
	 * 区
	 */
	@ApiModelProperty(value = "区")
	private Long areaId;

	/**
	 * 详细地址
	 */
	@ApiModelProperty(value = "详细地址")
	private String addressDetail;

	/**
	 * 是否默认仓 0：否，1：是
	 */
	@ApiModelProperty(value = "是否默认仓 0：否，1：是")
	private DefaultFlag defaultFlag;

	/**
	 * 线上仓:0 ,门店仓:1
	 */
	@ApiModelProperty(value = "线上仓:0 ,门店仓:1")
	private WareHouseType wareHouseType;


	/**
	 * 是否支持自提 0:否  , 1:是
	 */
	@ApiModelProperty(value = "是否支持自提 0:否  , 1:是")
	private PickUpFlag pickUpFlag;

	/**
	 * 创建时间
	 */
	@ApiModelProperty(value = "创建时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTime;

	/**
	 * 创建人
	 */
	@ApiModelProperty(value = "创建人")
	private String createPerson;

	/**
	 * 更新时间
	 */
	@ApiModelProperty(value = "更新时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime updateTime;

	/**
	 * 更新人
	 */
	@ApiModelProperty(value = "更新人")
	private String updatePerson;

	/**
	 * 仓库地区id(逗号分隔)
	 */
	@ApiModelProperty(value = "配送地id", notes = "逗号分隔")
	private String destinationArea;

	/**
	 * 仓库地区名称(逗号分隔)
	 */
	@ApiModelProperty(value = "配送地名称", notes = "逗号分隔")
	private String destinationAreaName;

	/**
	 * 已选区域
	 */
	@ApiModelProperty(value = "已选区域")
	private List<Long> selectedAreas;
	/**
	 * 已选区域 列表展示
	 */
	@ApiModelProperty(value = "已选区域")
	private List<WareHouseCityVO> wareHouseCityVOList;

    /**
     * 促销仓的id
     */
    @ApiModelProperty(value = "特价仓id")
    private String spPriceId;

	/**
	 * 自身的erpId
	 */
	@ApiModelProperty(value = "自身的erpId")
    private String selfErpId;

	/**
	 * 纬度
	 */
	@ApiModelProperty(value = "纬度")
	private Double lat;

	/**
	 * 经度
	 */
	@ApiModelProperty(value = "经度")
	private Double lng;

	@ApiModelProperty(value = "删除标志位")
	private DeleteFlag delFlag;

    @ApiModelProperty(value = "是否是线上仓，0：不是，1：是")
    private DefaultFlag type;

	@ApiModelProperty(value = "配送范围km")
	private Long distance;

	@ApiModelProperty(value = "是否没有库存")
	private DeleteFlag stockOutFlag;

}