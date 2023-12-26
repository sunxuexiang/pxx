package com.wanmi.sbc.pay.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/6/16 14:41
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CcbPayOrderRequest implements Serializable {

    private static final long serialVersionUID = 8694084559431338062L;

    /**
     * 主订单编号
     */
    private String mainOrderNo;

    /**
     * 订单总金额
     */
    private BigDecimal orderAmount;

    /**
     * 交易总金额
     */
    private BigDecimal txnAmt;

    /**
     * 子订单 列表
     */
    private List<CcbSubPayOrderRequest> subOrderList;

    private String clientIp;

    private Long channelId;

    private String payOrderNo;

    private String businessId;

    private String clrgDt;

    /**
     *  1 微信支付（微信小程序支付）
     *  2 支付宝支付 (支付宝小程序)
     *  3 好友代付（建行H5页面）
     *  4 鲸币充值二维码
     *  5 二维码支付
     *  6 建行对公支付
     *  7 广告支付二维码
     *  8 鲸币充值对公支付
     */
    private Integer payType;

    private String jsCode;
}
