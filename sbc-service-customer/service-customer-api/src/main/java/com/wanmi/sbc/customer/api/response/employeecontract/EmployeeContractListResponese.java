package com.wanmi.sbc.customer.api.response.employeecontract;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeContractListResponese implements Serializable {

    private static final long serialVersionUID = 4202920113830049792L;

     private List<EmployeeContractResponese> employeeContractResponeseList;
}
