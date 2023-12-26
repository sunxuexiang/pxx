package com.wanmi.sbc.customer;

import com.wanmi.sbc.account.api.request.AccountBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ModifyPwdRequest implements Serializable {

    private static final long serialVersionUID = 2123071731414023432L;

    @ApiModelProperty(value = "客户id")
    List<String> customerIds;
}
