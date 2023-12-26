package com.wanmi.sbc.pay.api.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/8/31 14:30
 */
@Data
public class CcbPayRecordResponse {
    /**
     * ID
     */
    @Id
    private Long recordId;

    /**
     * 业务订单编号
     */
    private String businessId;

    /**
     * 建行市场编码
     */
    private String mktId;

    /**
     * 主订单编码
     */
    private String mainOrdrNo;

    /**
     * 建行主订单编号
     */
    private String primOrdrNo;

    /**
     * 建行支付流水
     */
    private String pyTrnNo;

    /**
     * 建行支付类型
     */
    private String pymdCd;

    /**
     * 订单金额
     */
    private BigDecimal ordrTamt;

    /**
     * 实付金额
     */
    private BigDecimal txnTamt;

    /**
     * 清算日期 分账日期 = T+1
     */
    private String clrgDt;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 创建时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;


    /**
     * 00-分账处理成功,01-分账处理失败
     */
    private String subAccStcd;

    /**
     * 1 微信支付（微信小程序支付） 2 支付宝支付 (支付宝小程序)  3 好友代付（建行H5页面）4 鲸币充值 5 二维码支付 6 建行对公支付
     */
    private Integer payType;

    /**
     * 建行对公支付付款凭证
     */
    private String ccbPayImg;
}
