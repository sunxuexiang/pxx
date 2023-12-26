package com.wanmi.sbc.setting.api.request.navigationconfig;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import lombok.*;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>导航配置通用查询请求参数</p>
 * @author lvheng
 * @date 2021-04-12 14:46:18
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NavigationConfigQueryRequest extends BaseQueryRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 批量查询-idList
	 */
	@ApiModelProperty(value = "批量查询-idList")
	private List<Integer> idList;

	/**
	 * id
	 */
	@ApiModelProperty(value = "id")
	private Integer id;

	/**
	 * 导航名称
	 */
	@ApiModelProperty(value = "导航名称")
	private String navName;

	/**
	 * 导航图标-未点击状态
	 */
	@ApiModelProperty(value = "导航图标-未点击状态")
	private String iconShow;

	/**
	 * 导航图标-点击状态
	 */
	@ApiModelProperty(value = "导航图标-点击状态")
	private String iconClick;

}