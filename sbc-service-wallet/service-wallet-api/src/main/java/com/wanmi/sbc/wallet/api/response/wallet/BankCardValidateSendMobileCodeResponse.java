package com.wanmi.sbc.wallet.api.response.wallet;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description: 是否能够发送手机短信验证码response
 * @author: jiangxin
 * @create: 2021-08-23 20:29
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankCardValidateSendMobileCodeResponse implements Serializable {
    private static final long serialVersionUID = 5588943351081275573L;

    @ApiModelProperty(value = "是否可以发送验证码")
    private Boolean result;
}
