package com.wanmi.sbc.customer.api.request.employee;

import com.wanmi.sbc.common.enums.AccountType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>描述<p>
 *
 * @author zhaowei
 * @date 2021/6/10
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeSaveImgUrlByJobNoRequest implements Serializable {

    @ApiModelProperty(value = "工号")
    @NotNull
    private String jobNo;

    @ApiModelProperty(value = "账号类型")
    private AccountType accountType;

    @ApiModelProperty(value = "小程序图片")
    private String wechatImgUrl;
}
