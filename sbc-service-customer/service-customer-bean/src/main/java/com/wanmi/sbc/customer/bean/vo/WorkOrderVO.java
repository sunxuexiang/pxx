package com.wanmi.sbc.customer.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>工单VO</p>
 * @author baijz
 * @date 2020-05-17 16:03:15
 */
@ApiModel
@Data
public class WorkOrderVO implements Serializable {
	private static final long serialVersionUID = 1L;

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
	//注册的会员信息
	private CustomerVO customer;

	//已注册的会员信息
	private CustomerVO customerAlready;

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
	 * 修改时间
	 */
	@ApiModelProperty(value = "修改时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime modifyTime;

	/**
	 * 删除时间
	 */
	@ApiModelProperty(value = "删除时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime deleteTiime;

	@ApiModelProperty(value = "工单明细数量")
	private Long detailCount;
}