package com.wanmi.sbc.customer.api.request.employee;

import com.wanmi.sbc.customer.bean.vo.EmployeeCopyVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lm
 * @date 2022/09/17 11:14
 */
@ApiModel("业务员查询")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeCopyQueryRequest{


    @ApiModelProperty("省code")
    private String provinceCode;
}
