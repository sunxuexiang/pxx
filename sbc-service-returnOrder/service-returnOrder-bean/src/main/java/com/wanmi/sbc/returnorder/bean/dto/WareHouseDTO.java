package com.wanmi.sbc.returnorder.bean.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.PickUpFlag;
import com.wanmi.sbc.common.enums.WareHouseType;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.goods.bean.vo.WareHouseCityVO;
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
public class WareHouseDTO implements Serializable {


	private static final long serialVersionUID = -8506190124161728544L;
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
     * 特价仓id
     */
    @ApiModelProperty(value = "特价商品id")
    private String spPriceId;

}