package com.wanmi.sbc.pay.request;

import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.pay.bean.enums.TerminalType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import java.math.BigDecimal;

import static com.wanmi.sbc.common.util.ValidateUtil.NULL_EX_MESSAGE;

/**
 * 支付请求参数
 * Created by sunkun on 2017/8/10.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "支付请求对象")
public class PayRequest extends BaseRequest {

    private static final long serialVersionUID = -9011690769518722995L;
    /**
     * 订单id
     */
    @ApiModelProperty(value = "订单id，用于单笔订单支付场景，单笔支付时不能为空", dataType = "String", required = true)
    private String tid;

    /**
     * 父订单id
     */
    @ApiModelProperty(value = "父订单id，用于多笔订单合并支付场景，合并支付时不能为空", dataType = "String", required = true)
    private String parentTid;

    /**
     * 支付成功后的前端回调url
     */
    @ApiModelProperty(value = "支付成功后的前端回调url", dataType = "String")
    private String successUrl;

    /**
     * 支付渠道id
     */
    @NotNull
    @ApiModelProperty(value = "支付渠道id", dataType = "Long", required = true)
    private Long channelItemId;

    /**
     * 支付密码
     */
    @ApiModelProperty(value = "支付密码", dataType = "String", required = true)
    private String payPassword;

    /**
     * 微信支付时必传，付款用户在商户 appid 下的唯一标识。
     */
    @ApiModelProperty(value = "微信支付时必传，付款用户在商户 appid 下的唯一标识", dataType = "String")
    private String openId;


    @ApiModelProperty(value = "唤起支付宝时使用")
    private String origin;

    @Enumerated
    @ApiModelProperty(value = "终端类型",required = true, example="0:pc 1:h5 2:app")
    private TerminalType terminal = TerminalType.H5;

    /**
     * 商户id-boss端取默认值
     */
    @ApiModelProperty(value = "商户id-boss端取默认值")
    private Long storeId;

    @Override
    public void checkParam() {
        Validate.isTrue(StringUtils.isNotEmpty(tid) || StringUtils.isNotEmpty(parentTid), NULL_EX_MESSAGE,
                "tid | parentTid");
    }
}
