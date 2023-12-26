package com.wanmi.sbc.setting.provider.impl.advertising;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.advertising.service.AdvertisingRetailService;
import com.wanmi.sbc.setting.api.provider.advertising.AdvertisingRetailQueryProvider;
import com.wanmi.sbc.setting.api.request.advertising.AdvertisingRetailGetByIdRequest;
import com.wanmi.sbc.setting.api.request.advertising.AdvertisingRetailQueryRequest;
import com.wanmi.sbc.setting.api.response.advertising.AdvertisingRetailListResponse;
import com.wanmi.sbc.setting.api.response.advertising.AdvertisingRetailPageResponse;
import com.wanmi.sbc.setting.api.response.advertising.AdvertisingRetailResponse;
import com.wanmi.sbc.setting.bean.vo.AdvertisingRetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 散批广告位查询接口api实现类
 * @author: XinJiang
 * @time: 2022/4/19 11:14
 */
@RestController
@Validated
public class AdvertisingRetailQueryController implements AdvertisingRetailQueryProvider {

    @Autowired
    private AdvertisingRetailService advertisingRetailService;

    @Override
    public BaseResponse<AdvertisingRetailResponse> getById(AdvertisingRetailGetByIdRequest request) {
        return BaseResponse.success(KsBeanUtil.convert(advertisingRetailService.findById(request.getAdvertisingId()),AdvertisingRetailResponse.class));
    }

    @Override
    public BaseResponse<AdvertisingRetailPageResponse> page(AdvertisingRetailQueryRequest request) {
        return BaseResponse.success(AdvertisingRetailPageResponse.builder()
                .advertisingRetailPage(KsBeanUtil.convertPage(advertisingRetailService.advertisingPage(request), AdvertisingRetailVO.class))
                .build());
    }

    @Override
    public BaseResponse<AdvertisingRetailListResponse> list(AdvertisingRetailQueryRequest request) {
        return BaseResponse.success(AdvertisingRetailListResponse.builder()
                .advertisingRetailVOList(KsBeanUtil.convertList(advertisingRetailService.advertisingRetails(request), AdvertisingRetailVO.class))
                .build());
    }

    @Override
    public BaseResponse<AdvertisingRetailListResponse> listByCache() {
        return BaseResponse.success(AdvertisingRetailListResponse.builder()
                .advertisingRetailVOList(advertisingRetailService.getAdvertisingListByCache())
                .build());
    }


}
