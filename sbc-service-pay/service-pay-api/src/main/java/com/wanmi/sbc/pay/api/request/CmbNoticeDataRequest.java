package com.wanmi.sbc.pay.api.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "支付请求对象")
public class CmbNoticeDataRequest{

    /**
     * 商户发起支付请求的时间，精确到秒
     * 格式：yyyyMMddHHmmss
     */
    /**
     * 回调HTTP地址,支付请求时填写的支付结果通知地址
     */
    private String dateTime;
    /**
     * 商户订单日期
     * 格式：yyyyMMdd
     */
    private String date;
    /**
     *订单金额，格式：XXXX.XX
     */
    private String amount;
    /**
     *银行受理日期
     */
    private String bankDate;
    /**
     *商户订单号
     */
    private String orderNo;
    /**
     *单位为元，精确到小数点后两位。格式为：xxxx.xx元
     */
    private String discountAmount;
    /**
     *本接口固定为：“BKPAYRTN”
     */
    private String noticeType;
    /**
     *固定为“POST”
     */
    private String httpMethod;
    /**
     *卡类型,02：本行借记卡；
     * 03：本行贷记卡；
     * 08：他行借记卡；
     * 09：他行贷记卡
     */
    private String cardType;
    /**
     *银行通知序号,订单日期+订单号
     */
    private String noticeSerialNo;
    /**
     *原样返回商户在一网通支付请求报文中传送的成功支付结果通知附加参数
     */
    private String merchantPara;
    /**
     *优惠标志,Y:有优惠 N：无优惠
     */
    private String discountFlag;
    /**
     *银行订单流水号
     */
    private String bankSerialNo;
    /**
     *回调HTTP地址,支付请求时填写的支付结果通知地址
     */
    private String noticeUrl;
    /**
     *商户分行号，4位数字
     */
    private String branchNo;
    /**
     *商户号，6位数字
     */
    private String merchantNo;
}
