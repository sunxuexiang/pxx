package com.wanmi.sbc.setting.provider.impl.navigationconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.constant.RedisKeyConstant;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.navigationconfig.NavigationConfigProvider;
import com.wanmi.sbc.setting.api.request.navigationconfig.NavigationConfigDelByIdRequest;
import com.wanmi.sbc.setting.api.request.navigationconfig.NavigationConfigModifyRequest;
import com.wanmi.sbc.setting.api.request.navigationconfig.NavigationConfigQueryRequest;
import com.wanmi.sbc.setting.api.response.navigationconfig.NavigationConfigModifyResponse;
import com.wanmi.sbc.setting.navigationconfig.model.root.NavigationConfig;
import com.wanmi.sbc.setting.navigationconfig.service.NavigationConfigService;
import com.wanmi.sbc.setting.redis.RedisService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.Duration;
import java.util.List;

/**
 * <p>导航配置保存服务接口实现</p>
 * @author lvheng
 * @date 2021-04-12 14:46:18
 */
@RestController
@Validated
public class NavigationConfigController implements NavigationConfigProvider {
	@Autowired
	private NavigationConfigService navigationConfigService;

	@Autowired
	private RedisService redisService;

	@Override
	public BaseResponse<NavigationConfigModifyResponse> modify(@RequestBody @Valid NavigationConfigModifyRequest navigationConfigModifyRequest) {
		List<NavigationConfig> navigationConfig = KsBeanUtil.convert(navigationConfigModifyRequest.getNavigationConfig(), NavigationConfig.class);
		navigationConfigService.modify(navigationConfig);
		redisService.setObj(RedisKeyConstant.NAVIGATION_TAB_CONFIG_DATA, navigationConfig, Duration.ofDays(30).getSeconds());
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse delete(NavigationConfigDelByIdRequest navigationConfigDelByIdRequest) {

		navigationConfigService.delete(navigationConfigDelByIdRequest.getId());
		List<NavigationConfig> navigationConfigs = navigationConfigService.list(new NavigationConfigQueryRequest());
		//更新缓存
		if(CollectionUtils.isNotEmpty(navigationConfigs)){
			redisService.setObj(RedisKeyConstant.NAVIGATION_TAB_CONFIG_DATA, navigationConfigs, Duration.ofDays(30).getSeconds());
		}
		return BaseResponse.SUCCESSFUL();
	}


}

