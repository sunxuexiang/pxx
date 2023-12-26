package com.wanmi.sbc.customer.provider.impl.employee;

import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.employee.RoleInfoBaseProvider;
import com.wanmi.sbc.customer.api.response.base.BaseUtilResponse;
import com.wanmi.sbc.customer.api.response.employee.RoleBaseInfoQueryResponse;
import com.wanmi.sbc.customer.api.response.employee.RoleInfoQueryResponse;
import com.wanmi.sbc.customer.api.vo.RoleBaseInfoVO;
import com.wanmi.sbc.customer.employee.model.root.RoleInfo;
import com.wanmi.sbc.customer.employee.service.RoleInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@Validated
public class RoleInfoBaseController implements RoleInfoBaseProvider {

    @Autowired
    private RoleInfoService roleInfoService;

    /**
     * 根据角色ID查询角色信息
     *
     * @param roleInfoId {@link Long}
     * @return 角色信息 {@link RoleInfoQueryResponse}
     */
    @Override
    public BaseUtilResponse<RoleBaseInfoQueryResponse> getRoleBaseInfoById(@RequestBody @Valid Long roleInfoId) {
        Optional<RoleInfo> roleInfoOptional = roleInfoService.getRoleInfoById(roleInfoId);
        if (!roleInfoOptional.isPresent()) {
            return BaseUtilResponse.SUCCESSFUL();
        }
        RoleBaseInfoVO roleInfoVO = new RoleBaseInfoVO();
        RoleInfo roleInfo = roleInfoOptional.get();
        KsBeanUtil.copyPropertiesThird(roleInfo, roleInfoVO);
        RoleBaseInfoQueryResponse response = new RoleBaseInfoQueryResponse();
        response.setRoleInfoVO(roleInfoVO);
        return BaseUtilResponse.success(response);
    }
}
