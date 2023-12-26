package com.wanmi.sbc.setting.provider.impl.advertising;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.advertising.request.AdvertisingSaveRequest;
import com.wanmi.sbc.setting.advertising.request.StartPageAdvertisingSaveRequest;
import com.wanmi.sbc.setting.advertising.service.AdvertisingService;
import com.wanmi.sbc.setting.api.provider.advertising.AdvertisingProvider;
import com.wanmi.sbc.setting.api.request.advertising.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 首页广告位接口服务实现类
 * @author: XinJiang
 * @time: 2022/2/18 10:29
 */
@RestController
@Validated
public class AdvertisingController implements AdvertisingProvider {

    @Autowired
    private AdvertisingService advertisingService;

    @Override
    public BaseResponse add(AdvertisingAddRequest request) {
        advertisingService.addAdvertising(KsBeanUtil.convert(request,AdvertisingSaveRequest.class));

        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse modify(AdvertisingModifyRequest request) {
        advertisingService.modifyAdvertising(KsBeanUtil.convert(request, AdvertisingSaveRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse delById(AdvertisingDelByIdRequest request) {
        advertisingService.delById(request.getAdvertisingId(),request.getDelPerson());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse addStartPage(StartPageAdvertisingAddRequest request) {
        advertisingService.addStartPageAdvertising(KsBeanUtil.convert(request, StartPageAdvertisingSaveRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse modifyStartPage(StartPageAdvertisingAddRequest request) {
        advertisingService.modifyStartPageAdvertising(KsBeanUtil.convert(request, StartPageAdvertisingSaveRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse delByIdStartPage(StartPageAdvertisingDelByIdRequest request) {
        advertisingService.delStartPageAdvertisingById(request.getAdvertisingId(), request.getDelPerson());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse startPageModifyStatus(StartPageModifyStatusRequest request) {
        advertisingService.modifyStartPageStatus(request.getAdvertisingId(), request.getStatus());
        return BaseResponse.SUCCESSFUL();
    }
}
