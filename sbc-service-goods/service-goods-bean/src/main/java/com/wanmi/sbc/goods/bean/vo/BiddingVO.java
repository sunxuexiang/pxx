package com.wanmi.sbc.goods.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import java.time.LocalDateTime;

import com.wanmi.sbc.goods.bean.enums.ActivityStatus;
import com.wanmi.sbc.goods.bean.enums.BiddingType;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>竞价配置VO</p>
 * @author baijz
 * @date 2020-08-05 16:27:45
 */
@ApiModel
@Data
public class BiddingVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 竞价配置主键
	 */
	@ApiModelProperty(value = "竞价配置主键")
	private String biddingId;

	/**
	 * 关键字,分类
	 */
	@ApiModelProperty(value = "关键字,分类")
	private String keywords;

	/**
	 * 竞价类型0:关键字，1:分类
	 */
	@ApiModelProperty(value = "竞价类型0:关键字，1:分类")
	private BiddingType biddingType;

	/**
	 * 竞价的状态：0:未开始，1:进行中，2:已结束
	 */
	@ApiModelProperty(value = "竞价的状态：0:未开始，1:进行中，2:已结束")
	private ActivityStatus biddingStatus;

	/**
	 * 开始时间
	 */
	@ApiModelProperty(value = "开始时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime startTime;

	/**
	 * 修改时间
	 */
	@ApiModelProperty(value = "修改时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime modifyTime;

	/**
	 * 结束时间
	 */
	@ApiModelProperty(value = "结束时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime endTime;

	/**
	 * 竞价商品
	 */
	@ApiModelProperty(value = "竞价商品")
	private List<BiddingGoodsVO> biddingGoodsVOS;


	/**
	 * 删除标志位
	 */
	@ApiModelProperty(value = "删除标志位")
	private DeleteFlag delFlag;

}