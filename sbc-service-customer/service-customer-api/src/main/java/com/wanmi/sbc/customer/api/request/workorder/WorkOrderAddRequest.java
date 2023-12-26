package com.wanmi.sbc.customer.api.request.workorder;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import java.time.LocalDateTime;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.customer.api.request.CustomerBaseRequest;
import lombok.*;
import javax.validation.constraints.*;
import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>工单新增参数</p>
 * @author baijz
 * @date 2020-05-17 16:03:15
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderAddRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 工单号
	 */
	@ApiModelProperty(value = "工单号")
	@Length(max=20)
	private String workOrderNo;

	/**
	 * 社会信用代码
	 */
	@ApiModelProperty(value = "社会信用代码")
	@Length(max=30)
	private String socialCreditCode;

	/**
	 * 注册人Id
	 */
	@ApiModelProperty(value = "注册人Id")
	@Length(max=32)
	private String approvalCustomerId;

	/**
	 * 已注册会员的Id
	 */
	@ApiModelProperty(value = "已注册会员的Id")
	@Length(max=32)
	private String registedCustomerId;

	/**
	 * 账号合并状态
	 */
	@ApiModelProperty(value = "账号合并状态")
	@Max(127)
	private Integer accountMergeStatus;

	/**
	 * 状态 0:待处理，1：已完成
	 */
	@ApiModelProperty(value = "状态 0:待处理，1：已完成")
	@Max(127)
	private Integer status;

	/**
	 * 创建时间
	 */
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTime;

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

	/**
	 * 删除标志位
	 */
	@ApiModelProperty(value = "删除标志位", hidden = true)
	private DeleteFlag delFlag;

}