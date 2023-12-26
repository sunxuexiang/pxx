package com.wanmi.sbc.wallet.api.request.wallet;

import com.wanmi.sbc.wallet.api.request.BalanceBaseRequest;
import com.wanmi.sbc.wallet.bean.enums.SmsTemplate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description: 钱包银行卡-发送手机验证码request
 * @author: jiangxin
 * @create: 2021-08-23 19:54
 */
@ApiModel
@Data
public class BankCardSendMobilCodeRequest extends BalanceBaseRequest {

    private static final long serialVersionUID = 6245840085224914813L;

    @ApiModelProperty(value = "存入redis的验证码key")
    @NotBlank
    private String redisKey;

    @ApiModelProperty(value = "要发送短信的手机号码")
    @NotBlank
    private String mobile;

    @ApiModelProperty(value = "短信内容模版")
    @NotNull
    private SmsTemplate smsTemplate;
}
