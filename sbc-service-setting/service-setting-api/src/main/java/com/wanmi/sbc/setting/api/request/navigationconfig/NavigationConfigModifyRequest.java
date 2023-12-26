package com.wanmi.sbc.setting.api.request.navigationconfig;

import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.setting.bean.vo.NavigationConfigVO;
import lombok.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * <p>导航配置修改参数</p>
 * @author lvheng
 * @date 2021-04-12 14:46:18
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NavigationConfigModifyRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty("导航配置")
	List<NavigationConfigVO> navigationConfig;

}