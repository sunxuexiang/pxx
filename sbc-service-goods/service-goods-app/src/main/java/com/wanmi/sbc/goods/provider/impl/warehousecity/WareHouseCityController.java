package com.wanmi.sbc.goods.provider.impl.warehousecity;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.warehousecity.WareHouseCityProvider;
import com.wanmi.sbc.goods.api.request.warehousecity.WareHouseCityAddRequest;
import com.wanmi.sbc.goods.api.request.warehousecity.WareHouseCityDelByIdListRequest;
import com.wanmi.sbc.goods.api.request.warehousecity.WareHouseCityDelByIdRequest;
import com.wanmi.sbc.goods.api.request.warehousecity.WareHouseCityModifyRequest;
import com.wanmi.sbc.goods.api.response.warehousecity.WareHouseCityAddResponse;
import com.wanmi.sbc.goods.api.response.warehousecity.WareHouseCityModifyResponse;
import com.wanmi.sbc.goods.warehousecity.model.root.WareHouseCity;
import com.wanmi.sbc.goods.warehousecity.service.WareHouseCityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p> 仓库地区表保存服务接口实现</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:28:33
 */
@RestController
@Validated
public class WareHouseCityController implements WareHouseCityProvider {
	@Autowired
	private WareHouseCityService wareHouseCityService;

	@Override
	public BaseResponse<WareHouseCityAddResponse> add(@RequestBody @Valid WareHouseCityAddRequest wareHouseCityAddRequest) {
		WareHouseCity wareHouseCity = KsBeanUtil.convert(wareHouseCityAddRequest, WareHouseCity.class);
		return BaseResponse.success(new WareHouseCityAddResponse(
				wareHouseCityService.wrapperVo(wareHouseCityService.add(wareHouseCity))));
	}

	@Override
	public BaseResponse<WareHouseCityModifyResponse> modify(@RequestBody @Valid WareHouseCityModifyRequest wareHouseCityModifyRequest) {
		WareHouseCity wareHouseCity = KsBeanUtil.convert(wareHouseCityModifyRequest, WareHouseCity.class);
		return BaseResponse.success(new WareHouseCityModifyResponse(
				wareHouseCityService.wrapperVo(wareHouseCityService.modify(wareHouseCity))));
	}

	@Override
	public BaseResponse deleteById(@RequestBody @Valid WareHouseCityDelByIdRequest wareHouseCityDelByIdRequest) {
		wareHouseCityService.deleteById(wareHouseCityDelByIdRequest.getId());
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse deleteByIdList(@RequestBody @Valid WareHouseCityDelByIdListRequest wareHouseCityDelByIdListRequest) {
		wareHouseCityService.deleteByIdList(wareHouseCityDelByIdListRequest.getIdList());
		return BaseResponse.SUCCESSFUL();
	}

}

