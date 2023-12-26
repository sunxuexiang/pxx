package com.wanmi.sbc.goods.provider.impl.warehousecity;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.warehousecity.WareHouseCityQueryProvider;
import com.wanmi.sbc.goods.api.request.warehousecity.WareHouseCityByIdRequest;
import com.wanmi.sbc.goods.api.request.warehousecity.WareHouseCityListRequest;
import com.wanmi.sbc.goods.api.request.warehousecity.WareHouseCityPageRequest;
import com.wanmi.sbc.goods.api.request.warehousecity.WareHouseCityQueryRequest;
import com.wanmi.sbc.goods.api.response.warehousecity.WareHouseCityByIdResponse;
import com.wanmi.sbc.goods.api.response.warehousecity.WareHouseCityListResponse;
import com.wanmi.sbc.goods.api.response.warehousecity.WareHouseCityPageResponse;
import com.wanmi.sbc.goods.bean.vo.WareHouseCityVO;
import com.wanmi.sbc.goods.warehousecity.model.root.WareHouseCity;
import com.wanmi.sbc.goods.warehousecity.service.WareHouseCityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p> 仓库地区表查询服务接口实现</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:28:33
 */
@RestController
@Validated
public class WareHouseCityQueryController implements WareHouseCityQueryProvider {
	@Autowired
	private WareHouseCityService wareHouseCityService;

	@Override
	public BaseResponse<WareHouseCityPageResponse> page(@RequestBody @Valid WareHouseCityPageRequest wareHouseCityPageReq) {
		WareHouseCityQueryRequest queryReq = KsBeanUtil.convert(wareHouseCityPageReq, WareHouseCityQueryRequest.class);
		Page<WareHouseCity> wareHouseCityPage = wareHouseCityService.page(queryReq);
		Page<WareHouseCityVO> newPage = wareHouseCityPage.map(entity -> wareHouseCityService.wrapperVo(entity));
		MicroServicePage<WareHouseCityVO> microPage = new MicroServicePage<>(newPage, wareHouseCityPageReq.getPageable());
		WareHouseCityPageResponse finalRes = new WareHouseCityPageResponse(microPage);
		return BaseResponse.success(finalRes);
	}

	@Override
	public BaseResponse<WareHouseCityListResponse> list(@RequestBody @Valid WareHouseCityListRequest wareHouseCityListReq) {
		WareHouseCityQueryRequest queryReq = KsBeanUtil.convert(wareHouseCityListReq, WareHouseCityQueryRequest.class);
		List<WareHouseCity> wareHouseCityList = wareHouseCityService.list(queryReq);
		List<WareHouseCityVO> newList = wareHouseCityList.stream().map(entity -> wareHouseCityService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new WareHouseCityListResponse(newList));
	}

	@Override
	public BaseResponse<WareHouseCityByIdResponse> getById(@RequestBody @Valid WareHouseCityByIdRequest wareHouseCityByIdRequest) {
		WareHouseCity wareHouseCity =
		wareHouseCityService.getOne(wareHouseCityByIdRequest.getId());
		return BaseResponse.success(new WareHouseCityByIdResponse(wareHouseCityService.wrapperVo(wareHouseCity)));
	}

}

