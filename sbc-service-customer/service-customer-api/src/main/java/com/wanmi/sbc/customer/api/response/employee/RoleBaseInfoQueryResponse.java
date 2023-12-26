package com.wanmi.sbc.customer.api.response.employee;

import com.wanmi.sbc.customer.api.vo.RoleBaseInfoVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleBaseInfoQueryResponse implements Serializable {
    private static final long serialVersionUID = 1261280854938790287L;

    @ApiModelProperty(value = "角色信息")
    private RoleBaseInfoVO roleInfoVO;
}
