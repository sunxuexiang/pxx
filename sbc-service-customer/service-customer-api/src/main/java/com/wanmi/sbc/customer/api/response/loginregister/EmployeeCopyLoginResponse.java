package com.wanmi.sbc.customer.api.response.loginregister;

import com.wanmi.sbc.customer.bean.vo.EmployeeCopyVo;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lm
 * @date 2022/09/15 8:52
 */

@ApiModel
@Data
@Builder
public class EmployeeCopyLoginResponse extends EmployeeCopyVo {
    private static final long serialVersionUID = -5519120120296852262L;
}
