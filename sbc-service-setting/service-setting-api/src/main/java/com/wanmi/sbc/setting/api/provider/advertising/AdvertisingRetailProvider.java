package com.wanmi.sbc.setting.api.provider.advertising;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.advertising.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @description: 散批广告位接口api
 * @author: XinJiang
 * @time: 2022/4/19 10:41
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "AdvertisingRetailProvider")
public interface AdvertisingRetailProvider {

    /**
     * 新增散批广告位API
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/advertising/retail/add")
    BaseResponse add(@RequestBody @Valid AdvertisingRetailAddRequest request);

    /**
     * 修改散批广告位API
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/advertising/retail/modify")
    BaseResponse modify(@RequestBody @Valid AdvertisingRetailModifyRequest request);

    /**
     * 通过id删除广告位信息
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/advertising/retail/del-by-id")
    BaseResponse delById(@RequestBody @Valid AdvertisingRetailDelByIdRequest request);

    /**
     * 修改散批广告状态
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/advertising/retail/modify-status")
    BaseResponse modifyStatus(@RequestBody @Valid AdvertisingRetailModifyStatusRequest request);
}
