package com.wanmi.sbc.message.api.request.appmessage;

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
public class AppMessageUnreadRequest implements Serializable {

    private static final long serialVersionUID = -1043853905078952722L;
    /**
     * 会员id
     */
    @ApiModelProperty(value = "会员id")
    private String customerId;

}
