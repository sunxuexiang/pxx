package com.wanmi.sbc.customer.api.response.employee;

import com.wanmi.sbc.customer.api.vo.EmployeeBaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel
@Data
public class EmployeeByAccountNameAndTypeResponse implements Serializable {

    private static final long serialVersionUID = -7809034522048994351L;


    @ApiModelProperty(value = "员工")
    private EmployeeBaseVO employee;
}