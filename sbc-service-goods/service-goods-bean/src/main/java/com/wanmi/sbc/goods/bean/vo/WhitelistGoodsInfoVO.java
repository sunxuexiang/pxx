package com.wanmi.sbc.goods.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.EnableStatus;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>白名单VO</p>
 * @author zhangjinlong
 * @date 2020-03-24 14:59:50
 */
@ApiModel
@Data
public class WhitelistGoodsInfoVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 白名单主键
	 */
	@ApiModelProperty(value = "白名单主键")
	private Long whitelistId;

	/**
	 * 会员的Id
	 */
	@ApiModelProperty(value = "会员的Id")
	private String customerId;

	/**
	 * 货品的Id
	 */
	@ApiModelProperty(value = "货品的Id")
	private String goodsInfoId;

    /**
     * 店铺三级类目id
     */
    @ApiModelProperty(value = "店铺三级类目id")
    private Long storeCateId;

    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称")
    private String goodsInfoName;

    /**
     * sku编码
     */
    @ApiModelProperty(value = "sku编码")
    private String goodsInfoNo;

    /**
     * 市场价
     */
    @ApiModelProperty(value = "市场价")
    private BigDecimal marketPrice;

    /**
     * 类目名称
     */
    @ApiModelProperty(value = "类目名称")
    private String cateName;

	/**
	 * 商品的Id
	 */
	@ApiModelProperty(value = "商品的Id")
	private String goodsId;

	/**
	 * 门店的Id
	 */
	@ApiModelProperty(value = "门店的Id")
	private Long storeId;

	/**
	 * 启用的状态 0:禁用  1:启用
	 */
	@ApiModelProperty(value = "启用的状态 0:禁用  1:启用")
	private EnableStatus enableFlag;

	/**
	 * 创建时间
	 */
	@ApiModelProperty(value = "创建时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTime;

	/**
	 * 更新时间
	 */
	@ApiModelProperty(value = "更新时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime updateTime;

	/**
	 * 删除标记 0否 1是
	 */
	@ApiModelProperty(value = "删除标记 0否 1是")
	private DeleteFlag delFlag;

}