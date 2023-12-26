package com.wanmi.sbc.returnorder.bean.vo;


import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.PickUpFlag;
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
public class TradeWareHouseVO implements Serializable {


	private static final long serialVersionUID = -5778950032622605651L;
	/**
	 * wareId
	 */
	@ApiModelProperty(value = "wareId")
	private Long wareId;


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
	 * 市
	 */
	@ApiModelProperty(value = "市")
	private Long cityId;


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
	 * 是否支持自提 0:否  , 1:是
	 */
	@ApiModelProperty(value = "是否支持自提 0:否  , 1:是")
	private PickUpFlag pickUpFlag;


    /**
     * 特价仓id
     */
    @ApiModelProperty(value = "特价商品id")
    private String spPriceId;

	/**
	 * 自提码
	 */
	@ApiModelProperty(value = "自提码")
    private String pickUpCode;

}