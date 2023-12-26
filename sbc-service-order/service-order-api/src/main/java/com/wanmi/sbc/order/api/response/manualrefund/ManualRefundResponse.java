package com.wanmi.sbc.order.api.response.manualrefund;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.account.bean.enums.RefundStatus;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.order.bean.vo.ManualRefundImgVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 退款单
 */
@Data
public class ManualRefundResponse implements Serializable {

    private static final long serialVersionUID = -1185039413544891022L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "退款单id")
    private String refundId;

    /**
     * 会员详情ID外键
     */
    @ApiModelProperty(value = "会员详情ID外键")
    private String customerDetailId;

    /**
     * 退款平台：1-银联
     */
    @ApiModelProperty(value = "退款平台：1-银联")
    private Integer payType;

    /**
     * 订单总金额（实付）
     */
    @ApiModelProperty(value = "订单总金额（实付）")
    private BigDecimal totalPrice;

    /**
     * 申请退款金额
     */
    @ApiModelProperty(value = "申请退款金额")
    private BigDecimal applyPrice;

    /**
     * 订单编号
     */
    @ApiModelProperty(value = "订单编号")
    private String orderCode;

    /**
     * 支付单号
     */
    @ApiModelProperty(value = "支付单号")
    private String payOrderNo;

    /**
     * 退款单号
     */
    @ApiModelProperty(value = "退款单号")
    private String refundCode;

    /**
     * 退款单状态
     */
    @ApiModelProperty(value = "退款单状态")
    private RefundStatus refundStatus;

    /**
     * 拒绝原因
     */
    @ApiModelProperty(value = "拒绝原因")
    private String refuseReason;

    /**
     * del
     */
    @ApiModelProperty(value = "删除状态")
    private DeleteFlag delFlag;

    /**
     * 删除时间
     */
    @ApiModelProperty(value = "删除时间")
    private LocalDateTime delTime;

    /**
     * 创建时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 凭证地址
     */
    @ApiModelProperty(value = "凭证地址")
    private List<ManualRefundImgVO> manualRefundImgVOList;
}
