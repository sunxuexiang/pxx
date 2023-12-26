package com.wanmi.sbc.customer.api.request.workorder;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import java.time.LocalDateTime;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.customer.api.request.CustomerBaseRequest;
import lombok.*;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>工单通用查询请求参数</p>
 * @author baijz
 * @date 2020-05-17 16:03:15
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderQueryRequest extends BaseQueryRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 批量查询-工单IdList
	 */
	@ApiModelProperty(value = "批量查询-工单IdList")
	private List<String> workOrderIdList;

	/**
	 * 工单Id
	 */
	@ApiModelProperty(value = "工单Id")
	private String workOrderId;

	/**
	 * 工单号
	 */
	@ApiModelProperty(value = "工单号")
	private String workOrderNo;

	/**
	 * 社会信用代码
	 */
	@ApiModelProperty(value = "社会信用代码")
	private String socialCreditCode;

	/**
	 * 客户账号
	 */
	@ApiModelProperty(value = "客户账号")
	private String customerName;

	/**
	 * 客户联系方式
	 */
	@ApiModelProperty(value = "客户联系方式")
	private String customerAccount;


	/**
	 * 注册人Id
	 */
	@ApiModelProperty(value = "注册人Id")
	private String approvalCustomerId;

	/**
	 * 已注册会员的Id
	 */
	@ApiModelProperty(value = "已注册会员的Id")
	private String registedCustomerId;

	/**
	 * 账号合并状态
	 */
	@ApiModelProperty(value = "账号合并状态")
	private DefaultFlag accountMergeStatus;

	/**
	 * 状态 0:待处理，1：已完成
	 */
	@ApiModelProperty(value = "状态 0:待处理，1：已完成")
	private DefaultFlag status;

	/**
	 * 搜索条件:创建时间开始
	 */
	@ApiModelProperty(value = "搜索条件:创建时间开始")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTimeBegin;
	/**
	 * 搜索条件:创建时间截止
	 */
	@ApiModelProperty(value = "搜索条件:创建时间截止")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTimeEnd;

	@ApiModelProperty(value = "搜索条件:联系方式")
	private String contactPhone;

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
	 * 搜索条件:删除时间开始
	 */
	@ApiModelProperty(value = "搜索条件:删除时间开始")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime deleteTiimeBegin;
	/**
	 * 搜索条件:删除时间截止
	 */
	@ApiModelProperty(value = "搜索条件:删除时间截止")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime deleteTiimeEnd;

	/**
	 * 删除标志位
	 */
	@ApiModelProperty(value = "删除标志位")
	private DeleteFlag delFlag;

}