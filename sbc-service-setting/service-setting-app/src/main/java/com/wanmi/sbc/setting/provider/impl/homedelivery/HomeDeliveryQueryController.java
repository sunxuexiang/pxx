package com.wanmi.sbc.setting.provider.impl.homedelivery;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.Constants;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.homedelivery.HomeDeliveryQueryProvider;
import com.wanmi.sbc.setting.api.request.homedelivery.HomeDeliveryPageRequest;
import com.wanmi.sbc.setting.api.request.homedelivery.HomeDeliveryQueryRequest;
import com.wanmi.sbc.setting.api.response.homedelivery.HomeDeliveryPageResponse;
import com.wanmi.sbc.setting.api.request.homedelivery.HomeDeliveryListRequest;
import com.wanmi.sbc.setting.api.response.homedelivery.HomeDeliveryListResponse;
import com.wanmi.sbc.setting.api.request.homedelivery.HomeDeliveryByIdRequest;
import com.wanmi.sbc.setting.api.response.homedelivery.HomeDeliveryByIdResponse;
import com.wanmi.sbc.setting.bean.vo.HomeDeliveryVO;
import com.wanmi.sbc.setting.homedelivery.service.HomeDeliveryService;
import com.wanmi.sbc.setting.homedelivery.model.root.HomeDelivery;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

/**
 * <p>配送到家查询服务接口实现</p>
 * @author lh
 * @date 2020-08-01 14:13:32
 */
@RestController
@Validated
public class HomeDeliveryQueryController implements HomeDeliveryQueryProvider {
	@Autowired
	private HomeDeliveryService homeDeliveryService;

	@Override
	public BaseResponse<HomeDeliveryPageResponse> page(@RequestBody @Valid HomeDeliveryPageRequest homeDeliveryPageReq) {
		HomeDeliveryQueryRequest queryReq = KsBeanUtil.convert(homeDeliveryPageReq, HomeDeliveryQueryRequest.class);
		Page<HomeDelivery> homeDeliveryPage = homeDeliveryService.page(queryReq);
		Page<HomeDeliveryVO> newPage = homeDeliveryPage.map(entity -> homeDeliveryService.wrapperVo(entity));
		MicroServicePage<HomeDeliveryVO> microPage = new MicroServicePage<>(newPage, homeDeliveryPageReq.getPageable());
		HomeDeliveryPageResponse finalRes = new HomeDeliveryPageResponse(microPage);
		return BaseResponse.success(finalRes);
	}

	@Override
	public BaseResponse<HomeDeliveryListResponse> list(@RequestBody @Valid HomeDeliveryListRequest homeDeliveryListReq) {
		HomeDeliveryQueryRequest queryReq = KsBeanUtil.convert(homeDeliveryListReq, HomeDeliveryQueryRequest.class);
		if(queryReq.getStoreId() ==null){
			queryReq.setStoreId(Constants.BOSS_DEFAULT_STORE_ID);
		}
		List<HomeDelivery> homeDeliveryList = homeDeliveryService.list(queryReq);
		List<HomeDeliveryVO> newList = Arrays.asList(homeDeliveryService.wrapperVo(homeDeliveryList));
		List<HomeDelivery> deliveryStoreContent = homeDeliveryService.list(HomeDeliveryQueryRequest.builder().storeId(queryReq.getStoreId()).deliveryType(7).build());
		if(CollectionUtils.isNotEmpty(deliveryStoreContent)){
			newList.forEach(vo->{
				vo.setDeliveryToStoreContent(deliveryStoreContent.get(0).getContent());
			});
		}

		return BaseResponse.success(new HomeDeliveryListResponse(newList));
	}

	@Override
	public BaseResponse<HomeDeliveryByIdResponse> getById(@RequestBody @Valid HomeDeliveryByIdRequest homeDeliveryByIdRequest) {
		HomeDeliveryQueryRequest queryReq = KsBeanUtil.convert(homeDeliveryByIdRequest, HomeDeliveryQueryRequest.class);
		List<HomeDelivery> homeDeliveryList = homeDeliveryService.list(queryReq);
		HomeDeliveryVO homeDeliveryVO = homeDeliveryService.wrapperVo(homeDeliveryList);
		return BaseResponse.success(new HomeDeliveryByIdResponse(homeDeliveryVO));
	}

	@Override
	public BaseResponse<String> getByMenuId(HomeDeliveryListRequest homeDeliveryListReq) {
		HomeDeliveryQueryRequest queryReq = KsBeanUtil.convert(homeDeliveryListReq, HomeDeliveryQueryRequest.class);
		if(queryReq.getStoreId() ==null){
			queryReq.setStoreId(Constants.BOSS_DEFAULT_STORE_ID);
		}
		List<HomeDelivery> homeDeliveryList = homeDeliveryService.list(queryReq);
		String remark ="喜吖吖物流";
		if(CollectionUtils.isNotEmpty(homeDeliveryList)){
			remark = homeDeliveryList.get(0).getContent();
		}
		return BaseResponse.success(remark);
	}

	@Override
	public BaseResponse<HomeDeliveryByIdResponse> getBossCfg() {
		HomeDeliveryQueryRequest queryReq = HomeDeliveryQueryRequest.builder().storeId(Constants.BOSS_DEFAULT_STORE_ID).delFlag(DeleteFlag.NO).build();
		List<HomeDelivery> homeDeliveryList = homeDeliveryService.list(queryReq);
		if(CollectionUtils.isNotEmpty(homeDeliveryList)) {
			HomeDeliveryVO homeDeliveryVO = homeDeliveryService.wrapperVo(homeDeliveryList);
			return BaseResponse.success(HomeDeliveryByIdResponse.builder().homeDeliveryVO(homeDeliveryVO).build());
		}
		return BaseResponse.success(HomeDeliveryByIdResponse.builder().homeDeliveryVO(new HomeDeliveryVO()).build());
	}
}

