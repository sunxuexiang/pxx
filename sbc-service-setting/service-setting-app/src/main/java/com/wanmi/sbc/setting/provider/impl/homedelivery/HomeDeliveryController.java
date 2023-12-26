package com.wanmi.sbc.setting.provider.impl.homedelivery;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.homedelivery.HomeDeliveryProvider;
import com.wanmi.sbc.setting.api.request.homedelivery.HomeDeliveryModifyRequest;
import com.wanmi.sbc.setting.api.response.homedelivery.HomeDeliveryModifyResponse;
import com.wanmi.sbc.setting.homedelivery.service.HomeDeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p>配送到家保存服务接口实现</p>
 * @author lh
 * @date 2020-08-01 14:13:32
 */
@RestController
@Validated
public class HomeDeliveryController implements HomeDeliveryProvider {
	@Autowired
	private HomeDeliveryService homeDeliveryService;

	@Override
	public BaseResponse<HomeDeliveryModifyResponse> modify(@RequestBody @Valid HomeDeliveryModifyRequest homeDeliveryModifyRequest) {
		homeDeliveryService.modifyMessage(homeDeliveryModifyRequest);
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse<HomeDeliveryModifyResponse> modifyByDeliverType(HomeDeliveryModifyRequest homeDeliveryModifyRequest) {
		homeDeliveryService.updateByStoreIdIdAndDeliveryType(homeDeliveryModifyRequest.getStoreId(),homeDeliveryModifyRequest.getDeliveryType(),homeDeliveryModifyRequest.getContent());
		return BaseResponse.SUCCESSFUL();
	}
}

