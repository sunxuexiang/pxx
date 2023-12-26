package com.wanmi.sbc.trade.request;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import static com.wanmi.sbc.common.util.ValidateUtil.NULL_EX_MESSAGE;

/**
 * 微信扫码支付请求参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel
public class CcbPayRequest extends BaseRequest {

    private static final long serialVersionUID = -2855598681583948807L;

    /**
     * 订单id，单笔支付必传
     */
    @ApiModelProperty("订单号，若单笔支付必传")
    private String tid;

    /**
     * 父订单号，用于合并支付场景，合并支付必传
     */
    @ApiModelProperty("父订单号，合并支付必传")
    private String parentTid;

    /**
     * 支付渠道
     */
    private Long channelId;

    /**
     *  1 微信支付（微信小程序支付） 2 支付宝支付 (支付宝小程序)  3 好友代付（建行H5页面）4 鲸币充值 5 二维码支付 6 建行对公支付
     */
    private Integer payType;

    /**
     * 微信小程序支付 jsCode 或者  支付宝auth_code
     */
    private String jsCode;


    @Override
    public void checkParam() {
        Validate.isTrue(StringUtils.isNotEmpty(tid) || StringUtils.isNotEmpty(parentTid), NULL_EX_MESSAGE,
                "tid | parentTid");
    }
}
