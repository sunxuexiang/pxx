package com.wanmi.sbc.setting.provider.impl.platformaddress;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.platformaddress.PlatformAddressSaveProvider;
import com.wanmi.sbc.setting.api.request.platformaddress.PlatformAddressAddRequest;
import com.wanmi.sbc.setting.api.request.platformaddress.PlatformAddressDelByIdListRequest;
import com.wanmi.sbc.setting.api.request.platformaddress.PlatformAddressDelByIdRequest;
import com.wanmi.sbc.setting.api.request.platformaddress.PlatformAddressModifyRequest;
import com.wanmi.sbc.setting.api.response.platformaddress.PlatformAddressAddResponse;
import com.wanmi.sbc.setting.api.response.platformaddress.PlatformAddressModifyResponse;
import com.wanmi.sbc.setting.platformaddress.model.root.PlatformAddress;
import com.wanmi.sbc.setting.platformaddress.service.PlatformAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p>平台地址信息保存服务接口实现</p>
 * @author dyt
 * @date 2020-03-30 14:39:57
 */
@RestController
@Validated
public class PlatformAddressSaveController implements PlatformAddressSaveProvider {
	@Autowired
	private PlatformAddressService platformAddressService;

	@Override
	public BaseResponse<PlatformAddressAddResponse> add(@RequestBody @Valid PlatformAddressAddRequest platformAddressAddRequest) {
		PlatformAddress platformAddress = KsBeanUtil.convert(platformAddressAddRequest, PlatformAddress.class);
		return BaseResponse.success(new PlatformAddressAddResponse(
				platformAddressService.wrapperVo(platformAddressService.add(platformAddress))));
	}

	@Override
	public BaseResponse<PlatformAddressModifyResponse> modify(@RequestBody @Valid PlatformAddressModifyRequest platformAddressModifyRequest) {
		PlatformAddress platformAddress = platformAddressService.getOne(platformAddressModifyRequest.getId());
		KsBeanUtil.copyPropertiesThird(platformAddressModifyRequest, platformAddress);
		return BaseResponse.success(new PlatformAddressModifyResponse(
				platformAddressService.wrapperVo(platformAddressService.modify(platformAddress))));
	}

	@Override
	public BaseResponse deleteById(@RequestBody @Valid PlatformAddressDelByIdRequest platformAddressDelByIdRequest) {
		platformAddressService.deleteById(platformAddressDelByIdRequest.getId());
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse deleteByIdList(@RequestBody @Valid PlatformAddressDelByIdListRequest platformAddressDelByIdListRequest) {
		platformAddressService.deleteByIdList(platformAddressDelByIdListRequest.getIdList());
		return BaseResponse.SUCCESSFUL();
	}

}

