package com.wanmi.sbc.setting.provider.impl.advertising;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.advertising.model.root.Advertising;
import com.wanmi.sbc.setting.advertising.service.AdvertisingService;
import com.wanmi.sbc.setting.api.provider.advertising.AdvertisingQueryProvider;
import com.wanmi.sbc.setting.api.request.advertising.AdvertisingGetByIdRequest;
import com.wanmi.sbc.setting.api.request.advertising.AdvertisingQueryRequest;
import com.wanmi.sbc.setting.api.request.advertising.StartPageAdvertisingQueryRequest;
import com.wanmi.sbc.setting.api.response.advertising.*;
import com.wanmi.sbc.setting.bean.vo.AdvertisingVO;
import com.wanmi.sbc.setting.bean.vo.StartPageAdvertisingVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 首页广告位查询服务接口实现类
 * @author: XinJiang
 * @time: 2022/2/18 10:30
 */
@RestController
@Validated
public class AdvertisingQueryController implements AdvertisingQueryProvider {

    @Autowired
    private AdvertisingService advertisingService;

    @Override
    public BaseResponse<AdvertisingResponse> getById(AdvertisingGetByIdRequest request) {
        return BaseResponse.success(KsBeanUtil.convert(advertisingService.findById(request.getAdvertisingId()),AdvertisingResponse.class));
    }

    @Override
    public BaseResponse<AdvertisingPageResponse> page(AdvertisingQueryRequest request) {
        return BaseResponse.success(AdvertisingPageResponse.builder()
                .advertisingPage(KsBeanUtil.convertPage(advertisingService.advertisingPage(request), AdvertisingVO.class))
                .build());
    }

    @Override
    public BaseResponse<AdvertisingPageResponse> storePage(AdvertisingQueryRequest request) {
        return BaseResponse.success(AdvertisingPageResponse.builder()
                .advertisingPage(KsBeanUtil.convertPage(advertisingService.advertisingStorePage(request), AdvertisingVO.class))
                .build());
    }

    @Override
    public BaseResponse<AdvertisingListResponse> list(AdvertisingQueryRequest request) {
        return BaseResponse.success(AdvertisingListResponse.builder()
                .advertisingVOList(KsBeanUtil.convert(advertisingService.advertisingList(request),AdvertisingVO.class))
                .build());
    }

    @Override
    public BaseResponse<AdvertisingListResponse> listByCache(AdvertisingQueryRequest request) {
        return BaseResponse.success(AdvertisingListResponse.builder()
                .advertisingVOList(advertisingService.getAdvertisingListByCache(request.getWareId())).build());
    }

    @Override
    public BaseResponse<AdvertisingListResponse> listStoreIdByCache(AdvertisingQueryRequest request) {
        return BaseResponse.success(AdvertisingListResponse.builder()
                .advertisingVOList(advertisingService.getAdvertisingListStoreIdByCache(request.getStoreId())).build());
    }

    @Override
    public BaseResponse<StartPageAdvertisingResponse> getStartPageById(AdvertisingGetByIdRequest request) {
        return BaseResponse.success(KsBeanUtil.convert(advertisingService
                .findStartPageById(request.getAdvertisingId()), StartPageAdvertisingResponse.class));
    }

    @Override
    public BaseResponse<StartPageAdvertisingPageResponse> pageStartPage(StartPageAdvertisingQueryRequest request) {
        return BaseResponse.success(StartPageAdvertisingPageResponse.builder()
                .advertisingPage(KsBeanUtil.convertPage(advertisingService.startPageAdvertisingPage(request),StartPageAdvertisingVO.class))
                .build());
    }

    @Override
    public BaseResponse<StartPageAdvertisingResponse> getStartPageByCache() {
        return BaseResponse.success(KsBeanUtil.convert(advertisingService.getStartPageAdvertisingByCache(),StartPageAdvertisingResponse.class));
    }
}
