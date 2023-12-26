package com.wanmi.sbc.customer.api.response.employee;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeIdResponse implements Serializable {
    private static final long serialVersionUID = -5382415034994061544L;

    /**
     * 员工的Id
     */
    @ApiModelProperty(value = "员工的Id")
    private String employeeId;
}
