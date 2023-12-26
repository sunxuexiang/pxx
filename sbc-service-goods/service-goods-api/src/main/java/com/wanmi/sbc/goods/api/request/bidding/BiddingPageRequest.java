package com.wanmi.sbc.goods.api.request.bidding;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import java.time.LocalDateTime;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.goods.bean.enums.ActivityStatus;
import com.wanmi.sbc.goods.bean.enums.BiddingType;
import lombok.*;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>竞价配置分页查询请求参数</p>
 * @author baijz
 * @date 2020-08-05 16:27:45
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiddingPageRequest extends BaseQueryRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 批量查询-竞价配置主键List
	 */
	@ApiModelProperty(value = "批量查询-竞价配置主键List")
	private List<String> biddingIdList;

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
	 * 搜索条件:开始时间开始
	 */
	@ApiModelProperty(value = "搜索条件:开始时间开始")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime startTimeBegin;
	/**
	 * 搜索条件:开始时间截止
	 */
	@ApiModelProperty(value = "搜索条件:开始时间截止")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime startTimeEnd;

	/**
	 * 搜索条件:修改时间开始
	 */
	@ApiModelProperty(value = "搜索条件:修改时间开始")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime modifyTimeBegin;
	/**
	 * 搜索条件:修改时间截止
	 */
	@ApiModelProperty(value = "搜索条件:修改时间截止")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime modifyTimeEnd;

	/**
	 * 搜索条件:结束时间开始
	 */
	@ApiModelProperty(value = "搜索条件:结束时间开始")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime endTimeBegin;
	/**
	 * 搜索条件:结束时间截止
	 */
	@ApiModelProperty(value = "搜索条件:结束时间截止")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime endTimeEnd;

	/**
	 * 删除标志位
	 */
	@ApiModelProperty(value = "删除标志位")
	private DeleteFlag delFlag;

}