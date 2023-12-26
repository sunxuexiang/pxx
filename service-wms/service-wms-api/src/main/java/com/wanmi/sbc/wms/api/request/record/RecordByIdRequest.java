package com.wanmi.sbc.wms.api.request.record;

import com.wanmi.sbc.wms.api.request.WmsBaseRequest;
import lombok.*;
import javax.validation.constraints.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>单个查询请求记录请求参数</p>
 * @author baijz
 * @date 2020-05-06 19:23:45
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordByIdRequest extends WmsBaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 记录主键
	 */
	@ApiModelProperty(value = "记录主键")
	@NotNull
	private Long recordId;
}