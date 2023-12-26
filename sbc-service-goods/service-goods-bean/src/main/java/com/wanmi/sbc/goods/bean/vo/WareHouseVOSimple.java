package com.wanmi.sbc.goods.bean.vo;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.PickUpFlag;
import com.wanmi.sbc.common.enums.WareHouseType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>仓库表VO</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:21:37
 */
@ApiModel
@Data
public class WareHouseVOSimple implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * wareId
	 */
	@ApiModelProperty(value = "wareId")
	private Long wareId;

	/**
	 * wareId
	 */
	@ApiModelProperty(value = "散批仓库ID")
	private String bulkWareId;

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
	 * 特价仓id
	 */
	@ApiModelProperty(value = "特价商品id")
	private String spPriceId;

}