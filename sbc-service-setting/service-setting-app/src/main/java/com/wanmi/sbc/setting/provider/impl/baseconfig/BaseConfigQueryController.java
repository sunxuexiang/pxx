package com.wanmi.sbc.setting.provider.impl.baseconfig;

//import com.wanmi.open.sdk.response.BaseConfigRopResponse;
import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.setting.api.response.baseconfig.*;
import com.wanmi.sbc.setting.systemcancellationpolicy.model.root.SystemCancellationPolicy;
import com.wanmi.sbc.setting.systemcancellationpolicy.service.SystemCancellationPolicyService;
import com.wanmi.sbc.setting.systemprivacypolicy.model.root.SystemPrivacyPolicy;
import com.wanmi.sbc.setting.systemprivacypolicy.service.SystemPrivacyPolicyService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.baseconfig.BaseConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.baseconfig.BaseConfigPageRequest;
import com.wanmi.sbc.setting.api.request.baseconfig.BaseConfigQueryRequest;
import com.wanmi.sbc.setting.api.request.baseconfig.BaseConfigListRequest;
import com.wanmi.sbc.setting.api.request.baseconfig.BaseConfigByIdRequest;
import com.wanmi.sbc.setting.bean.vo.BaseConfigVO;
import com.wanmi.sbc.setting.baseconfig.service.BaseConfigService;
import com.wanmi.sbc.setting.baseconfig.model.root.BaseConfig;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>基本设置查询服务接口实现</p>
 * @author lq
 * @date 2019-11-05 16:08:31
 */
@RestController
@Validated
public class BaseConfigQueryController implements BaseConfigQueryProvider {
	@Autowired
	private BaseConfigService baseConfigService;
	@Autowired
	private SystemPrivacyPolicyService systemPrivacyPolicyService;

	@Autowired
	private SystemCancellationPolicyService systemCancellationPolicyService;

	@Override
	public BaseResponse<BaseConfigPageResponse> page(@RequestBody @Valid BaseConfigPageRequest baseConfigPageReq) {
		BaseConfigQueryRequest queryReq = new BaseConfigQueryRequest();
		KsBeanUtil.copyPropertiesThird(baseConfigPageReq, queryReq);
		Page<BaseConfig> baseConfigPage = baseConfigService.page(queryReq);
		Page<BaseConfigVO> newPage = baseConfigPage.map(entity -> baseConfigService.wrapperVo(entity));
		MicroServicePage<BaseConfigVO> microPage = new MicroServicePage<>(newPage, baseConfigPageReq.getPageable());
		BaseConfigPageResponse finalRes = new BaseConfigPageResponse(microPage);
		return BaseResponse.success(finalRes);
	}

	@Override
	public BaseResponse<BaseConfigListResponse> list(@RequestBody @Valid BaseConfigListRequest baseConfigListReq) {
		BaseConfigQueryRequest queryReq = new BaseConfigQueryRequest();
		KsBeanUtil.copyPropertiesThird(baseConfigListReq, queryReq);
		List<BaseConfig> baseConfigList = baseConfigService.list(queryReq);
		List<BaseConfigVO> newList = baseConfigList.stream().map(entity -> baseConfigService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new BaseConfigListResponse(newList));
	}

	@Override
	public BaseResponse<BaseConfigByIdResponse> getById(@RequestBody @Valid BaseConfigByIdRequest baseConfigByIdRequest) {
		BaseConfig baseConfig = baseConfigService.getById(baseConfigByIdRequest.getBaseConfigId());
		return BaseResponse.success(new BaseConfigByIdResponse(baseConfigService.wrapperVo(baseConfig)));
	}

	@Override
	public BaseResponse<BaseConfigRopResponse> getBaseConfig(){
		BaseConfigRopResponse response = new BaseConfigRopResponse();
		List<BaseConfig> baseConfigs = baseConfigService.list(new BaseConfigQueryRequest());
		List<BaseConfigVO> newList = baseConfigs.stream().map(entity -> baseConfigService.wrapperVo(entity)).collect(Collectors.toList());
		if(newList.size()>0){
			KsBeanUtil.copyPropertiesThird(newList.get(0),response);
			//查询用户隐私协议
			SystemPrivacyPolicy systemPrivacyPolicy = systemPrivacyPolicyService.querySystemPrivacyPolicy();
			if(Objects.nonNull(systemPrivacyPolicy)){
				response.setPrivacyContent(systemPrivacyPolicy.getPrivacyPolicy());
				//用户协议
				if(!StringUtils.isEmpty(response.getRegisterContent())){
					response.setCustomerContent(response.getRegisterContent());
				}
			}
			//查询用户注销须知
			SystemCancellationPolicy systemCancellationPolicy = systemCancellationPolicyService.querySystemCancellationPolicy();
			if (Objects.nonNull(systemCancellationPolicy)) {
				response.setCancellationContent(systemCancellationPolicy.getCancellationPolicy());
			}
		}
		return BaseResponse.success(response);
	}

	@Override
	public BaseResponse<BossLogoResponse> queryBossLogo(){
		String logo = baseConfigService.queryBossLogo();
		return BaseResponse.success(new BossLogoResponse(logo));
	}
}

