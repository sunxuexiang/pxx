package com.wanmi.sbc.setting.provider.impl.activityconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.activityconfig.model.root.ActivityConfig;
import com.wanmi.sbc.setting.activityconfig.service.ActivityConfigService;
import com.wanmi.sbc.setting.api.provider.activityconfig.ActivityConfigProvider;
import com.wanmi.sbc.setting.api.request.activityconfig.ActivityConfigAddRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>导航配置保存服务接口实现</p>
 * @author lvheng
 * @date 2021-04-19 18:49:30
 */
@RestController
@Validated
public class ActivityConfigController implements ActivityConfigProvider {
	@Autowired
	private ActivityConfigService activityConfigService;

	@Override
	public BaseResponse addAll(@RequestBody @Valid ActivityConfigAddRequest request) {

		List<ActivityConfig> list = activityConfigService.list();
		for (ActivityConfig config : list) {
			switch (config.getName()) {
				case "isOpen":
					config.setValue(request.getIsOpen());
					break;
				case "fullReductionIcon":
					config.setValue(request.getFullReductionIcon());
					break;
				case "onceReductionIcon":
					config.setValue(request.getOnceReductionIcon());
					break;
				case "discountIcon":
					config.setValue(request.getDiscountIcon());
					break;
				case "discountGiftIcon":
					config.setValue(request.getDiscountGiftIcon());
				default:
					break;

			}
		}
		activityConfigService.addAll(list);
		return BaseResponse.SUCCESSFUL();
	}

}

