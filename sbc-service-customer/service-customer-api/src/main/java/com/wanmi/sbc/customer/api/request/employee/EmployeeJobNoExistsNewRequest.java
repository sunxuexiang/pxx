package com.wanmi.sbc.customer.api.request.employee;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeJobNoExistsNewRequest implements Serializable {
    private static final long serialVersionUID = -1631237537817169853L;

    /**
     * 工号
     */
    @ApiModelProperty(value = "工号")
    @NotNull
    private String jobNo;

}
