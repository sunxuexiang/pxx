package com.wanmi.sbc.setting.api.provider.villagesaddress;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.villagesaddress.VillagesAddressConfigQueryRequest;
import com.wanmi.sbc.setting.api.response.villagesaddress.VillagesAddressConfigListResponse;
import com.wanmi.sbc.setting.api.response.villagesaddress.VillagesAddressConfigPageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * @Description: 乡镇件地址配置查询provider
 * @Author: XinJiang
 * @Date: 2022/4/29 10:23
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "VillagesAddressConfigQueryProvider")
public interface VillagesAddressConfigQueryProvider {

    /**
     * 分页获取配置信息
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/villages/page")
    BaseResponse<VillagesAddressConfigPageResponse> page(@RequestBody @Valid VillagesAddressConfigQueryRequest request);

    /**
     * 分页获取配置信息
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/villages/get-count-by-all-id")
    BaseResponse<Integer> getCountByAllId(@RequestBody @Valid VillagesAddressConfigQueryRequest request);

    /**
     * 根据条件获取乡镇件配置地址信息
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/villages/list")
    BaseResponse<VillagesAddressConfigListResponse> list(@RequestBody @Valid VillagesAddressConfigQueryRequest request);

    /**
     * 根据条件获取乡镇件配置地址信息
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/villages/findListVillageByCityList")
    BaseResponse<VillagesAddressConfigListResponse> findListVillageByCityList(@RequestBody  VillagesAddressConfigQueryRequest request);

    @PostMapping("/setting/${application.setting.version}/villages/findListVillageByVillageIdList")
    BaseResponse<VillagesAddressConfigListResponse> findListVillageByVillageIdList(@RequestBody VillagesAddressConfigQueryRequest request);

    @GetMapping("/setting/${application.setting.version}/villages/get-count-by-villageId-storeId")
    BaseResponse<Integer> getCountByVillageIdAndStoreId(@RequestParam(value="villageId") Long villageId,@RequestParam(value="storeId") Long storeId);

}
