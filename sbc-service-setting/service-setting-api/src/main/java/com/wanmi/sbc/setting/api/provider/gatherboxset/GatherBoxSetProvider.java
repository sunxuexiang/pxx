package com.wanmi.sbc.setting.api.provider.gatherboxset;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.advertising.AdvertisingAddRequest;
import com.wanmi.sbc.setting.api.request.gatherboxset.GatherBoxSetAddRequest;
import com.wanmi.sbc.setting.api.response.gatherboxset.GatherBoxSetBannerResponse;
import com.wanmi.sbc.setting.api.response.gatherboxset.GatherBoxSetInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "GatherBoxSetProvider")
public interface GatherBoxSetProvider {

    /**
     * 凑箱设置
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/gatherBoxSet/add")
    BaseResponse add(@RequestBody @Valid GatherBoxSetAddRequest request);

    /**
     * 获取凑箱设置信息
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/gatherBoxSet/info")
    BaseResponse<GatherBoxSetInfoResponse> getGatherBoxSetInfo();

    /**
     * 获取凑箱设置信息
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/gatherBoxSet/banner")
    BaseResponse<GatherBoxSetBannerResponse> getBulkBanner();
}
