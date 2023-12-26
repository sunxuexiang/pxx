package com.wanmi.sbc.setting.api.provider.advertising;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.advertising.AdvertisingRetailGetByIdRequest;
import com.wanmi.sbc.setting.api.request.advertising.AdvertisingRetailQueryRequest;
import com.wanmi.sbc.setting.api.response.advertising.AdvertisingRetailListResponse;
import com.wanmi.sbc.setting.api.response.advertising.AdvertisingRetailPageResponse;
import com.wanmi.sbc.setting.api.response.advertising.AdvertisingRetailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @description: 散批广告位查询接口api
 * @author: XinJiang
 * @time: 2022/4/19 11:13
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "AdvertisingRetailQueryProvider")
public interface AdvertisingRetailQueryProvider {

    /**
     * 通过id获取散批广告位信息
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/advertising/retail/get-by-id")
    BaseResponse<AdvertisingRetailResponse> getById(@RequestBody @Valid AdvertisingRetailGetByIdRequest request);

    /**
     * 分页获取散批广告位信息
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/advertising/retail/page")
    BaseResponse<AdvertisingRetailPageResponse> page(@RequestBody @Valid AdvertisingRetailQueryRequest request);

    /**
     * 获取散批广告位列表信息
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/advertising/retail/list")
    BaseResponse<AdvertisingRetailListResponse> list(@RequestBody @Valid AdvertisingRetailQueryRequest request);

    /**
     * 获取散批广告位列表信息（缓存级）
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/advertising/retail/list-by-cache")
    BaseResponse<AdvertisingRetailListResponse> listByCache();

}
