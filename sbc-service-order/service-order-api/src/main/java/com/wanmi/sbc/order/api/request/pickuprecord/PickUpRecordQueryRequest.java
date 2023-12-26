package com.wanmi.sbc.order.api.request.pickuprecord;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>测试代码生成通用查询请求参数</p>
 * @author lh
 * @date 2020-07-14 13:48:26
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickUpRecordQueryRequest extends BaseQueryRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 批量查询-主键List
	 */
	@ApiModelProperty(value = "批量查询-主键List")
	private List<String> pickUpIdList;

	/**
	 * 主键
	 */
	@ApiModelProperty(value = "主键")
	private String pickUpId;

	/**
	 * 店铺id
	 */
	@ApiModelProperty(value = "店铺id")
	private Long storeId;

	/**
	 * 订单id
	 */
	@ApiModelProperty(value = "订单id")
	private String tradeId;

	/**
	 * 自提码
	 */
	@ApiModelProperty(value = "自提码")
	private String pickUpCode;

	/**
	 * 是否已自提:0:未自提 1：已自提
	 */
	@ApiModelProperty(value = "是否已自提:0:未自提 1：已自提")
	private DefaultFlag pickUpFlag;

	/**
	 * 搜索条件:自提时间开始
	 */
	@ApiModelProperty(value = "搜索条件:自提时间开始")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime pickUpTimeBegin;
	/**
	 * 搜索条件:自提时间截止
	 */
	@ApiModelProperty(value = "搜索条件:自提时间截止")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime pickUpTimeEnd;

	/**
	 * 手机号
	 */
	@ApiModelProperty(value = "手机号")
	private String contactPhone;

	/**
	 * 删除标志位:0:未删除1：以上处
	 */
	@ApiModelProperty(value = "删除标志位:0:未删除1：以上处")
	private DeleteFlag delFlag;

	/**
	 * 搜索条件:创建表时间开始
	 */
	@ApiModelProperty(value = "搜索条件:创建表时间开始")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTimeBegin;
	/**
	 * 搜索条件:创建表时间截止
	 */
	@ApiModelProperty(value = "搜索条件:创建表时间截止")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTimeEnd;

}