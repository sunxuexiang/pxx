package com.wanmi.sbc.live.api.provider.host;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.live.api.request.host.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>主播服务Provider</p>
 */
@FeignClient(value = "${application.live.name}", url="${feign.url.live:#{null}}",contextId = "LiveHostProvider")
public interface LiveHostProvider {

    /**
     * 保存主播
     * @param addRequest
     * @return
     */
    @PostMapping("/host/${application.live.version}/liveHost/add")
    BaseResponse add(@RequestBody LiveHostAddRequest addRequest);

    /**
     * 更新主播
     * @param modifyRequest
     * @return
     */
    @PostMapping("/host/${application.live.version}/liveHost/modify")
    BaseResponse modify(@RequestBody LiveHostModifyRequest modifyRequest);

    /**
     * 删除主播
     * @param request
     * @return
     */
    @PostMapping("/host/${application.live.version}/liveHost/delete")
    BaseResponse delete(@RequestBody LiveHostDeleteRequest request);

    /**
     * 启用主播
     * @param request
     * @return
     */
    @PostMapping("/host/${application.live.version}/liveHost/enable")
    BaseResponse enable(@RequestBody LiveHostEnableRequest request);

    /**
     * 主播离职
     * @param request
     * @return
     */
    @PostMapping("/host/${application.live.version}/liveHost/leave")
    BaseResponse leave(@RequestBody LiveHostLeaveRequest request);

    /**
     * 根据账户信息查询主播信息
     * @param request
     * @return
     */
    @PostMapping("/host/${application.live.version}/liveHost/getInfoByCustomer")
    BaseResponse getInfoByCustomer(@RequestBody LiveHostInfoRequest request);

    /**
     * 根据ID查询主播信息
     * @param hostId
     * @return
     */
    @PostMapping("/host/${application.live.version}/liveHost/getInfo")
    BaseResponse getInfo(Integer hostId);

    /**
     * 获取所有已启用个账户列表
     * @return
     */
    @PostMapping("/host/${application.live.version}/liveHost/getEnableCustomerAccountList")
    BaseResponse<List<String>> getEnableCustomerAccountList();


    /**
     * 查询主播分页
     * @param pageRequest
     * @return
     */
    @PostMapping("/host/${application.live.version}/liveHost/getPage")
    BaseResponse getPage(@RequestBody LiveHostPageRequest pageRequest);
}