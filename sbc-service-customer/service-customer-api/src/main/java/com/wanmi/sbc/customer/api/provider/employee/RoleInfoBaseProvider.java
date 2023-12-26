package com.wanmi.sbc.customer.api.provider.employee;

import com.wanmi.sbc.customer.api.response.base.BaseUtilResponse;
import com.wanmi.sbc.customer.api.response.employee.RoleBaseInfoQueryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "${application.customer.name}",url="${feign.url.customer:#{null}}", contextId = "RoleInfoBaseProvider")
public interface RoleInfoBaseProvider {


    /**
     * 根据角色ID查询角色信息
     *
     * @param roleInfoId {@link Long}
     * @return 角色信息 {@link RoleBaseInfoQueryResponse}
     */
    @PostMapping("/customer/${application.customer.version}/role/query-by-role-info-id")
    BaseUtilResponse<RoleBaseInfoQueryResponse> getRoleBaseInfoById(@RequestBody @Valid Long roleInfoId);
}
