package com.wanmi.sbc.customer.api.response.employeecontract;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.customer.bean.vo.EmployeePageVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.io.Serializable;

@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeContractPageResponese implements Serializable {

    private static final long serialVersionUID = 4202920113830049792L;
    private MicroServicePage<EmployeeContractResponese> pageVo;

}
