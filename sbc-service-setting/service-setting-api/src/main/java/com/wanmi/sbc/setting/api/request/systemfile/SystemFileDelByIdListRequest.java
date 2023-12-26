package com.wanmi.sbc.setting.api.request.systemfile;

import com.wanmi.sbc.setting.api.request.SettingBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * 批量删除平台文件请求参数
 * @author hudong
 * @date 2023-09-08 16:12:49
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemFileDelByIdListRequest extends SettingBaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 批量删除-IDList
	 */
	@ApiModelProperty(value = "批量删除-IDList")
	@NotEmpty
	private List<Long> ids;

}