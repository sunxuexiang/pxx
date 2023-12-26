package com.wanmi.sbc.account.api.response.wallet;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description: 会员登录注册-发送手机验证码Response
 * @author: jiangxin
 * @create: 2021-08-23 20:17
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankCardSendMobilCodeResponse implements Serializable {

    private static final long serialVersionUID = -5093911798315406990L;

    @ApiModelProperty(value = "是否可以发送验证码")
    private Integer result;
}
