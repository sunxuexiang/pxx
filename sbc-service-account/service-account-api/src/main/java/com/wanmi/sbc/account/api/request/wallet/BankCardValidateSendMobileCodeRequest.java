package com.wanmi.sbc.account.api.request.wallet;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Description: 钱包银行卡是否可以发送短信验证请求
 * @author: jiangxin
 * @create: 2021-08-23 20:34
 */
@ApiModel
@Data
public class BankCardValidateSendMobileCodeRequest implements Serializable {

    private static final long serialVersionUID = -4636763167147660222L;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    @NotBlank
    private String mobile;
}
