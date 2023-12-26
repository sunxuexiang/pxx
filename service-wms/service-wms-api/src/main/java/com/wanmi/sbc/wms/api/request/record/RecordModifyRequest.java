package com.wanmi.sbc.wms.api.request.record;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import java.time.LocalDateTime;
import com.wanmi.sbc.wms.api.request.WmsBaseRequest;
import lombok.*;
import javax.validation.constraints.*;
import org.hibernate.validator.constraints.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>请求记录修改参数</p>
 * @author baijz
 * @date 2020-05-06 19:23:45
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordModifyRequest extends WmsBaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 记录主键
	 */
	@ApiModelProperty(value = "记录主键")
	@Max(9223372036854775807L)
	private Long recordId;

	/**
	 * 请求类型
	 */
	@ApiModelProperty(value = "请求类型")
	@Max(127)
	private Integer method;

	/**
	 * 请求的地址
	 */
	@ApiModelProperty(value = "请求的地址")
	@Length(max=255)
	private String requestUrl;

	/**
	 * 请求的实体
	 */
	@ApiModelProperty(value = "请求的实体")
	private String requestBody;

	/**
	 * 返回的信息
	 */
	@ApiModelProperty(value = "返回的信息")
	private String resposeInfo;

	/**
	 * 返回的状态
	 */
	@ApiModelProperty(value = "返回的状态")
	@Length(max=32)
	private String status;

	/**
	 * 请求时间
	 */
	@ApiModelProperty(value = "请求时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTime;

}