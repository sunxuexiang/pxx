package com.wanmi.sbc.customer.api.response.parentcustomerrela;

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
@NoArgsConstructor
@AllArgsConstructor
public class CustomerMergeFlagResponse implements Serializable {


    private static final long serialVersionUID = -353753187022338545L;

    @ApiModelProperty(value = "导入账号标志位 true可以导入")
    private Boolean mergeAccountFlag;
}
