package com.wanmi.sbc.setting.provider.impl.platformaddress;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.platformaddress.PlatformAddressQueryProvider;
import com.wanmi.sbc.setting.api.request.platformaddress.PlatformAddressByIdRequest;
import com.wanmi.sbc.setting.api.request.platformaddress.PlatformAddressListRequest;
import com.wanmi.sbc.setting.api.request.platformaddress.PlatformAddressPageRequest;
import com.wanmi.sbc.setting.api.request.platformaddress.PlatformAddressQueryRequest;
import com.wanmi.sbc.setting.api.response.platformaddress.PlatformAddressByIdResponse;
import com.wanmi.sbc.setting.api.response.platformaddress.PlatformAddressListResponse;
import com.wanmi.sbc.setting.api.response.platformaddress.PlatformAddressPageResponse;
import com.wanmi.sbc.setting.bean.vo.PlatformAddressVO;
import com.wanmi.sbc.setting.platformaddress.model.root.PlatformAddress;
import com.wanmi.sbc.setting.platformaddress.service.PlatformAddressService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>平台地址信息查询服务接口实现</p>
 * @author dyt
 * @date 2020-03-30 14:39:57
 */
@RestController
@Validated
public class PlatformAddressQueryController implements PlatformAddressQueryProvider {
	@Autowired
	private PlatformAddressService platformAddressService;

	@Override
	public BaseResponse<PlatformAddressPageResponse> page(@RequestBody @Valid PlatformAddressPageRequest platformAddressPageReq) {
		PlatformAddressQueryRequest queryReq = KsBeanUtil.convert(platformAddressPageReq, PlatformAddressQueryRequest.class);
		Page<PlatformAddress> platformAddressPage = platformAddressService.page(queryReq);
		Page<PlatformAddressVO> newPage = platformAddressPage.map(entity -> platformAddressService.wrapperVo(entity));
		MicroServicePage<PlatformAddressVO> microPage = new MicroServicePage<>(newPage, platformAddressPageReq.getPageable());
		PlatformAddressPageResponse finalRes = new PlatformAddressPageResponse(microPage);
		return BaseResponse.success(finalRes);
	}

	@Override
	public BaseResponse<PlatformAddressListResponse> list(@RequestBody @Valid PlatformAddressListRequest platformAddressListReq) {
		PlatformAddressQueryRequest queryReq = KsBeanUtil.convert(platformAddressListReq, PlatformAddressQueryRequest.class);
		List<PlatformAddress> platformAddressList = platformAddressService.list(queryReq);

        Set<String> parenAddIds = new HashSet<>();
		if(Boolean.TRUE.equals(platformAddressListReq.getLeafFlag()) && CollectionUtils.isNotEmpty(platformAddressList)){
            List<String> addrIds = platformAddressList.stream().map(PlatformAddress::getAddrId).collect(Collectors.toList());
            List<PlatformAddress> childList = platformAddressService.list(PlatformAddressQueryRequest.builder().addrParentIdList(addrIds).delFlag(DeleteFlag.NO).build());
            if(CollectionUtils.isNotEmpty(childList)){
                parenAddIds.addAll(childList.stream().map(PlatformAddress::getAddrParentId).distinct().collect(Collectors.toSet()));
            }
        }

		List<PlatformAddressVO> newList = platformAddressList.stream().map(entity -> {
            PlatformAddressVO vo = platformAddressService.wrapperVo(entity);
            if(parenAddIds.contains(vo.getAddrId())){
                vo.setLeafFlag(Boolean.FALSE);
            }
            return vo;
		}).collect(Collectors.toList());
		return BaseResponse.success(new PlatformAddressListResponse(newList));
	}

	@Override
	public BaseResponse<PlatformAddressByIdResponse> getById(@RequestBody @Valid PlatformAddressByIdRequest platformAddressByIdRequest) {
		PlatformAddress platformAddress =
		platformAddressService.getOne(platformAddressByIdRequest.getId());
		return BaseResponse.success(new PlatformAddressByIdResponse(platformAddressService.wrapperVo(platformAddress)));
	}

	@Override
	public BaseResponse<PlatformAddressListResponse> provinceOrCitylist(@RequestBody @Valid PlatformAddressListRequest platformAddressListReq) {
		List<PlatformAddress> platformAddressList = platformAddressService.provinceCityList(platformAddressListReq.getAddrIdList());
		List<PlatformAddressVO> newList = platformAddressList.stream().map(entity -> {
			PlatformAddressVO vo = platformAddressService.wrapperVo(entity);
			return vo;
		}).collect(Collectors.toList());
		return BaseResponse.success(new PlatformAddressListResponse(newList));
	}

}

