package com.wanmi.sbc.setting.api.request.activityconfig;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import lombok.*;

/**
 * <p>导航配置新增参数</p>
 *
 * @author lvheng
 * @date 2021-04-19 18:49:30
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityConfigAddRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 开关
	 */
	private String isOpen;

	/**
	 * 满减
	 */
	private String fullReductionIcon;

	/**
	 * 立减
	 */
	private String onceReductionIcon;

	/**
	 * 买折
	 */
	private String discountIcon;

	/**
	 * 买赠
	 */
	private String discountGiftIcon;

}