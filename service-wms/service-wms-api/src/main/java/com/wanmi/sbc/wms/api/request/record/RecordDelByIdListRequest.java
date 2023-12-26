package com.wanmi.sbc.wms.api.request.record;

import com.wanmi.sbc.wms.api.request.WmsBaseRequest;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>批量删除请求记录请求参数</p>
 * @author baijz
 * @date 2020-05-06 19:23:45
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordDelByIdListRequest extends WmsBaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 批量删除-记录主键List
	 */
	@ApiModelProperty(value = "批量删除-记录主键List")
	@NotEmpty
	private List<Long> recordIdList;
}