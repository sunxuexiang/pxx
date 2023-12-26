package com.wanmi.sbc.pay.api.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CmbPayRefundDataResponse implements Serializable {

    @ApiModelProperty(value = "处理结果,SUC0000：请求处理成功 其他：请求处理失败 ")
    private String rspCode;

    @ApiModelProperty(value = "详细信息,请求处理失败时返回错误描述")
    private String rspMsg;

    @ApiModelProperty(value = "响应时间,银行返回该数据的时间 格式：yyyyMMddHHmmss")
    private String dateTime;

    @ApiModelProperty(value = "银行的退款流水号")
    private String bankSerialNo;

    @ApiModelProperty(value = "退款币种,固定为：10")
    private String currency;

    @ApiModelProperty(value = "退款金额,格式：xxxx.xx")
    private String amount;

    @ApiModelProperty(value = "银行的退款参考号")
    private String refundRefNo;

    @ApiModelProperty(value = "退款受理日期格式：yyyyMMdd")
    private String bankDate;

    @ApiModelProperty(value = "退款受理时间格式：HHmmss")
    private String bankTime;

    @ApiModelProperty(value = "商户上送流水号")
    private String refundSerialNo;

    @ApiModelProperty(value = "实际退款金额,格式：xxxx.xx")
    private String settleAmount;

    @ApiModelProperty(value = "退回优惠金额,格式：xxxx.xx")
    private String discountAmount;

}
