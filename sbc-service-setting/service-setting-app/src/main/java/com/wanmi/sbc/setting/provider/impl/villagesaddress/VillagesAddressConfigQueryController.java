package com.wanmi.sbc.setting.provider.impl.villagesaddress;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.villagesaddress.VillagesAddressConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.villagesaddress.VillagesAddressConfigQueryRequest;
import com.wanmi.sbc.setting.api.response.villagesaddress.VillagesAddressConfigListResponse;
import com.wanmi.sbc.setting.api.response.villagesaddress.VillagesAddressConfigPageResponse;
import com.wanmi.sbc.setting.bean.vo.VillagesAddressConfigVO;
import com.wanmi.sbc.setting.villagesaddress.service.VillagesAddressConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 乡镇件地址配置信息查询接口实现类
 * @author: XinJiang
 * @time: 2022/4/29 11:26
 */
@RestController
@Validated
public class VillagesAddressConfigQueryController implements VillagesAddressConfigQueryProvider {

    @Autowired
    private VillagesAddressConfigService villagesAddressConfigService;

    @Override
    public BaseResponse<VillagesAddressConfigPageResponse> page(VillagesAddressConfigQueryRequest request) {
        Page<VillagesAddressConfigVO> villagePage = KsBeanUtil.convertPage(villagesAddressConfigService.page(request), VillagesAddressConfigVO.class);
        MicroServicePage<VillagesAddressConfigVO> microPage = new MicroServicePage<>(villagePage,villagePage.getPageable());
        return BaseResponse.success(VillagesAddressConfigPageResponse.builder().pageVillages(microPage).build());
    }

    @Override
    public BaseResponse<Integer> getCountByAllId(VillagesAddressConfigQueryRequest request) {
        return BaseResponse.success(villagesAddressConfigService.getCountByAllId(request.getProvinceId(),request.getCityId(),request.getAreaId(),request.getVillageId(),request.getStoreId()));
    }

    @Override
    public BaseResponse<VillagesAddressConfigListResponse> list(VillagesAddressConfigQueryRequest request) {
        return BaseResponse.success(VillagesAddressConfigListResponse.builder()
                .villagesAddressConfigVOList(KsBeanUtil.convert(villagesAddressConfigService.findAll(request),VillagesAddressConfigVO.class))
                .build());
    }

    @Override
    public BaseResponse<VillagesAddressConfigListResponse> findListVillageByCityList(VillagesAddressConfigQueryRequest request) {
        return BaseResponse.success(VillagesAddressConfigListResponse.builder()
                .villagesAddressConfigVOList(KsBeanUtil.convert(villagesAddressConfigService.findListVillageByCityList(request),VillagesAddressConfigVO.class))
                .build());
    }

    public BaseResponse<VillagesAddressConfigListResponse> findListVillageByVillageIdList(VillagesAddressConfigQueryRequest request) {
        return BaseResponse.success(VillagesAddressConfigListResponse.builder()
                .villagesAddressConfigVOList(KsBeanUtil.convert(villagesAddressConfigService.findListVillageByVillageIdList(request),VillagesAddressConfigVO.class))
                .build());
    }

    @Override
    public BaseResponse<Integer> getCountByVillageIdAndStoreId(Long villageId, Long storeId) {
        return BaseResponse.success(villagesAddressConfigService.getCountByVillageIdAndStoreId(villageId,storeId));
    }
}
