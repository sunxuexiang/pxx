package com.wanmi.sbc.setting.api.provider.advertising;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.advertising.AdvertisingGetByIdRequest;
import com.wanmi.sbc.setting.api.request.advertising.AdvertisingQueryRequest;
import com.wanmi.sbc.setting.api.request.advertising.StartPageAdvertisingQueryRequest;
import com.wanmi.sbc.setting.api.response.advertising.*;
import com.wanmi.sbc.setting.bean.vo.AdvertisingVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @Description: 首页广告位查询服务Provider
 * @Author: XinJiang
 * @Date: 2022/2/18 10:20
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "AdvertisingQueryProvider")
public interface AdvertisingQueryProvider {

    /**
     * 通过id获取首页广告位信息
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/advertising/get-by-id")
    BaseResponse<AdvertisingResponse> getById(@RequestBody @Valid AdvertisingGetByIdRequest request);

    /**
     * 分页获取首页广告位信息
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/advertising/page")
    BaseResponse<AdvertisingPageResponse> page(@RequestBody @Valid AdvertisingQueryRequest request);

    /**
     * 分页获取商家端首页广告位信息
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/advertising/storePage")
    BaseResponse<AdvertisingPageResponse> storePage(@RequestBody @Valid AdvertisingQueryRequest request);


    /**
     * 获取首页广告位列表信息
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/advertising/list")
    BaseResponse<AdvertisingListResponse> list(@RequestBody @Valid AdvertisingQueryRequest request);

    /**
     * 获取首页广告位列表信息（缓存级）
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/advertising/list-by-cache")
    BaseResponse<AdvertisingListResponse> listByCache(@RequestBody @Valid AdvertisingQueryRequest request);

    @PostMapping("/setting/${application.setting.version}/advertising/list-store-id-by-cache")
    BaseResponse<AdvertisingListResponse> listStoreIdByCache(@RequestBody @Valid AdvertisingQueryRequest request);

    /**
     * 通过id获取首页广告位信息
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/advertising/get-start-page-by-id")
    BaseResponse<StartPageAdvertisingResponse> getStartPageById(@RequestBody @Valid AdvertisingGetByIdRequest request);

    /**
     * 分页获取首页广告位信息
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/advertising/page-start-page")
    BaseResponse<StartPageAdvertisingPageResponse> pageStartPage(@RequestBody @Valid StartPageAdvertisingQueryRequest request);

    /**
     * 获取首页广告位列表信息（缓存级）
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/advertising/get-start-page-by-cache")
    BaseResponse<StartPageAdvertisingResponse> getStartPageByCache();

}
