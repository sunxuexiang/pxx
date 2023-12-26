package com.wanmi.sbc.setting.provider.impl.activityconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.activityconfig.model.root.ActivityConfig;
import com.wanmi.sbc.setting.activityconfig.service.ActivityConfigService;
import com.wanmi.sbc.setting.api.provider.activityconfig.ActivityConfigQueryProvider;
import com.wanmi.sbc.setting.api.response.activityconfig.ActivityConfigListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>导航配置查询服务接口实现</p>
 * @author lvheng
 * @date 2021-04-19 18:49:30
 */
@RestController
@Validated
public class ActivityConfigQueryController implements ActivityConfigQueryProvider {
	@Autowired
	private ActivityConfigService activityConfigService;

	@Override
	public BaseResponse<ActivityConfigListResponse> list() {
		List<ActivityConfig> activityConfigList = activityConfigService.list();
		ActivityConfigListResponse response = new ActivityConfigListResponse();
		activityConfigList.stream().forEach(entity -> {
			switch (entity.getName()) {
				case "isOpen":
					response.setIsOpen(entity.getValue());
					break;
				case "fullReductionIcon":
					response.setFullReductionIcon(entity.getValue());
					break;
				case "onceReductionIcon":
					response.setOnceReductionIcon(entity.getValue());
					break;
				case "discountIcon":
					response.setDiscountIcon(entity.getValue());
					break;
				case "discountGiftIcon":
					response.setDiscountGiftIcon(entity.getValue());
				default:
					break;

			}
		});
		return BaseResponse.success(response);
	}

}

