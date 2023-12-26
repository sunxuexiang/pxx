package com.wanmi.sbc.setting.api.provider.advertising;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.advertising.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @Description: 首页广告位服务Provider
 * @Author: XinJiang
 * @Date: 2022/2/18 10:18
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "AdvertisingProvider")
public interface AdvertisingProvider {

    /**
     * 新增首页广告位API
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/advertising/add")
    BaseResponse add(@RequestBody @Valid AdvertisingAddRequest request);

    /**
     * 修改首页广告位API
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/advertising/modify")
    BaseResponse modify(@RequestBody @Valid AdvertisingModifyRequest request);

    /**
     * 通过id删除广告位信息
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/advertising/del-by-id")
    BaseResponse delById(@RequestBody @Valid AdvertisingDelByIdRequest request);

    /**
     * 新增启动页广告位API
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/advertising/add-start-page")
    BaseResponse addStartPage(@RequestBody @Valid StartPageAdvertisingAddRequest request);

    /**
     * 修改启动页广告位API
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/advertising/modify-start-page")
    BaseResponse modifyStartPage(@RequestBody @Valid StartPageAdvertisingAddRequest request);

    /**
     * 通过id删除启动页广告信息
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/advertising/start/page/del-by-id")
    BaseResponse delByIdStartPage(@RequestBody @Valid StartPageAdvertisingDelByIdRequest request);

    /**
     * 修改启动页广告状态
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/advertising/start/page/modify-status")
    BaseResponse startPageModifyStatus(@RequestBody @Valid StartPageModifyStatusRequest request);
}
