package com.wanmi.sbc.setting.api.request.payswitch;

import com.wanmi.sbc.common.enums.AppPayType;
import com.wanmi.sbc.common.enums.EnableStatus;
import com.wanmi.sbc.setting.api.request.SettingBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@ApiModel
public class AppPaySwitchParam extends SettingBaseRequest {

    /**
     * 支付类型：alipay 支付宝；wechatPay 微信支付；friendPay 好友代付
     */
    @ApiModelProperty(value = "支付类型：alipay 支付宝；wechatPay 微信支付；friendPay 好友代付；bjLoan 白鲸借款；bjRepayment 白鲸还款")
    private AppPayType payType;

    @ApiModelProperty(value = "安卓显示开关: 0 关闭；1 启用")
    private Integer androidStatus;

    @ApiModelProperty(value = "IOS显示开关 0 关闭；1 启用")
    private Integer iosStatus;

    @ApiModelProperty(value = "支付通道 0 建行；1 银联 2 微信公众号")
    private Integer channel = 0;

    @ApiModelProperty(value = "支付方式名称")
    private String payName;

    @ApiModelProperty(value = "安卓可用开关: 0 关闭；1 启用")
    private Integer androidUsableStatus;

    @ApiModelProperty(value = "IOS可用开关 0 关闭；1 启用")
    private Integer iosUsableStatus;

    @ApiModelProperty(value = "不可用的提示")
    private String disableHint;

}
