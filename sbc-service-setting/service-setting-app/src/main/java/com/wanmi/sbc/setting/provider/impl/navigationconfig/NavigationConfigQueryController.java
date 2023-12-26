package com.wanmi.sbc.setting.provider.impl.navigationconfig;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.navigationconfig.NavigationConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.navigationconfig.NavigationConfigPageRequest;
import com.wanmi.sbc.setting.api.request.navigationconfig.NavigationConfigQueryRequest;
import com.wanmi.sbc.setting.api.response.navigationconfig.NavigationConfigPageResponse;
import com.wanmi.sbc.setting.api.request.navigationconfig.NavigationConfigListRequest;
import com.wanmi.sbc.setting.api.response.navigationconfig.NavigationConfigListResponse;
import com.wanmi.sbc.setting.api.request.navigationconfig.NavigationConfigByIdRequest;
import com.wanmi.sbc.setting.api.response.navigationconfig.NavigationConfigByIdResponse;
import com.wanmi.sbc.setting.bean.vo.NavigationConfigVO;
import com.wanmi.sbc.setting.navigationconfig.service.NavigationConfigService;
import com.wanmi.sbc.setting.navigationconfig.model.root.NavigationConfig;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>导航配置查询服务接口实现</p>
 * @author lvheng
 * @date 2021-04-12 14:46:18
 */
@RestController
@Validated
public class NavigationConfigQueryController implements NavigationConfigQueryProvider {
	@Autowired
	private NavigationConfigService navigationConfigService;

	@Override
	public BaseResponse<NavigationConfigPageResponse> page(@RequestBody @Valid NavigationConfigPageRequest navigationConfigPageReq) {
		NavigationConfigQueryRequest queryReq = KsBeanUtil.convert(navigationConfigPageReq, NavigationConfigQueryRequest.class);
		Page<NavigationConfig> navigationConfigPage = navigationConfigService.page(queryReq);
		Page<NavigationConfigVO> newPage = navigationConfigPage.map(entity -> navigationConfigService.wrapperVo(entity));
		MicroServicePage<NavigationConfigVO> microPage = new MicroServicePage<>(newPage, navigationConfigPageReq.getPageable());
		NavigationConfigPageResponse finalRes = new NavigationConfigPageResponse(microPage);
		return BaseResponse.success(finalRes);
	}

	@Override
	public BaseResponse<NavigationConfigListResponse> list(@RequestBody @Valid NavigationConfigListRequest navigationConfigListReq) {
		NavigationConfigQueryRequest queryReq = KsBeanUtil.convert(navigationConfigListReq, NavigationConfigQueryRequest.class);
		List<NavigationConfig> navigationConfigList = navigationConfigService.list(queryReq);
		List<NavigationConfigVO> newList = navigationConfigList.stream().map(entity -> navigationConfigService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new NavigationConfigListResponse(newList));
	}

	@Override
	public BaseResponse<NavigationConfigByIdResponse> getById(@RequestBody @Valid NavigationConfigByIdRequest navigationConfigByIdRequest) {
		NavigationConfig navigationConfig =
		navigationConfigService.getOne(navigationConfigByIdRequest.getId());
		return BaseResponse.success(new NavigationConfigByIdResponse(navigationConfigService.wrapperVo(navigationConfig)));
	}

}

