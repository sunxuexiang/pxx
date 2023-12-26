package com.wanmi.sbc.setting.provider.impl.advertising;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.advertising.request.AdvertisingRetailSaveRequest;
import com.wanmi.sbc.setting.advertising.service.AdvertisingRetailService;
import com.wanmi.sbc.setting.api.provider.advertising.AdvertisingRetailProvider;
import com.wanmi.sbc.setting.api.request.advertising.AdvertisingRetailAddRequest;
import com.wanmi.sbc.setting.api.request.advertising.AdvertisingRetailDelByIdRequest;
import com.wanmi.sbc.setting.api.request.advertising.AdvertisingRetailModifyRequest;
import com.wanmi.sbc.setting.api.request.advertising.AdvertisingRetailModifyStatusRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 散批广告位操作api实现类
 * @author: XinJiang
 * @time: 2022/4/19 10:53
 */
@RestController
@Validated
public class AdvertisingRetailController implements AdvertisingRetailProvider {

    @Autowired
    private AdvertisingRetailService advertisingRetailService;

    @Override
    public BaseResponse add(AdvertisingRetailAddRequest request) {
        advertisingRetailService.addAdvertisingRetail(KsBeanUtil.convert(request, AdvertisingRetailSaveRequest.class));
        advertisingRetailService.fillRedis();
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse modify(AdvertisingRetailModifyRequest request) {
        advertisingRetailService.modifyAdvertisingRetail(KsBeanUtil.convert(request, AdvertisingRetailSaveRequest.class));
        advertisingRetailService.fillRedis();
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse delById(AdvertisingRetailDelByIdRequest request) {
        advertisingRetailService.delById(request.getAdvertisingId(), request.getDelPerson());
        advertisingRetailService.fillRedis();
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse modifyStatus(AdvertisingRetailModifyStatusRequest request) {
        advertisingRetailService.modifyStatus(request.getAdvertisingId(), request.getStatus(), request.getAdvertisingType());
        advertisingRetailService.fillRedis();
        return BaseResponse.SUCCESSFUL();
    }
}
