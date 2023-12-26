package com.wanmi.sbc.setting.api.provider.invitation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 请求
 * Created by daiyitian on 2017/3/24.
 */
@ApiModel
@Data
public class InvitationConfigRequest implements Serializable{

    @ApiModelProperty(value = "新客限购")
    private Integer newCustomersBuyLimit;

    @ApiModelProperty(value = "老客限购")
    private Integer oldCustomersBuyLimit;

    @ApiModelProperty(value = "邀请规则")
    private String invitationRules;

}
