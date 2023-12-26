package com.wanmi.sbc.customer.api.response.fadada;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.customer.api.response.employeecontract.EmployeeContractResponese;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serializable;

@ApiModel
@Data
@NoArgsConstructor
public class BelugaMallContractPageResponese implements Serializable {

    private static final long serialVersionUID = 4202920113830049792L;
    private MicroServicePage<BelugaMallContractResponese> pageVo;
}
