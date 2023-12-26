package com.wanmi.sbc.setting.api.request.activityconfig;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import java.time.LocalDateTime;
import com.wanmi.sbc.common.base.BaseRequest;
import lombok.*;
import javax.validation.constraints.*;
import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>导航配置修改参数</p>
 * @author lvheng
 * @date 2021-04-19 18:49:30
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityConfigModifyRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 活动配置id
	 */
	@ApiModelProperty(value = "活动配置id")
	@Max(9223372036854775807L)
	private Long id;

	/**
	 * 配置名称
	 */
	@ApiModelProperty(value = "配置名称")
	@Length(max=45)
	private String name;

	/**
	 * 配置别名
	 */
	@ApiModelProperty(value = "配置别名")
	@Length(max=45)
	private String alias;

	/**
	 * 配置别名
	 */
	@ApiModelProperty(value = "配置别名")
	@Length(max=500)
	private String value;

	/**
	 * 创建时间
	 */
	@ApiModelProperty(value = "创建时间", hidden = true)
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTime;

	/**
	 * 更新人id
	 */
	@ApiModelProperty(value = "更新人id", hidden = true)
	private String updatePerson;

}