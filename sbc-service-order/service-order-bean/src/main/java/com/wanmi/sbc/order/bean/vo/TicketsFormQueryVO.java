package com.wanmi.sbc.order.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.account.bean.vo.CustomerWalletVO;
import com.wanmi.sbc.account.bean.vo.TicketsFormLogVo;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketsFormQueryVO implements Serializable {
    private static final long serialVersionUID = -4503314078923621080L;
    @ApiModelProperty(value = "form_id")
    private Long formId;
    /**
     * 钱包id
     */
    @ApiModelProperty(value = "钱包id")
    private Long walletId;

    /**
     * 虚拟商品id
     */
    @ApiModelProperty(value = "虚拟商品id")
    private Integer virtualGoodsId;

    /**
     * 银行名称
     */
    @ApiModelProperty(value = "银行名称")
    private String bankName;

    /**
     * 支行
     */
    @ApiModelProperty(value = "支行")
    private String bankBranch;

    /**
     * 银行卡号
     */
    @ApiModelProperty(value = "银行卡号")
    private String bankCode;

    private String bankNo;

    /**
     * 工单类型
     */
    @ApiModelProperty(value = "apply_type")
    private Integer applyType;

    /**
     * 申请金额
     */
    @ApiModelProperty(value = "申请金额")
    private BigDecimal applyPrice;

    /**
     * 申请时间
     */
    @ApiModelProperty(value = "申请时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime applyTime;

    /**
     * 充值申请单状态【1待审核，2充值成功，3充值失败】
     */
    @ApiModelProperty(value = "充值申请单状态【1待审核，2充值成功，3充值失败】")
    private Integer rechargeStatus;

    /**
     * 提现申请单状态【1待审核，2已审核，3已打款，4已拒绝】
     */
    @ApiModelProperty(value = "提现申请单状态【1待审核，2已审核，3已打款，4已拒绝】")
    private Integer extractStatus;

    /**
     * 到账金额
     */
    @ApiModelProperty(value = "到账金额")
    private BigDecimal arrivalPrice;

    /**
     * 审核时间
     */
    @ApiModelProperty(value = "审核时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime auditTime;

    /**
     * 审核人
     */
    @ApiModelProperty(value = "审核人")
    private String auditAdmin;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 交易单号
     */
    @ApiModelProperty(value = "交易单号")
    private String recordNo;


    @ApiModelProperty(value = "钱包")
    private CustomerWalletVO customerWallet;


    @ApiModelProperty(value = "提现申请日志")
    private List<TicketsFormLogVo> ticketsFormLogVos;

    //==========花花要求============//
    @ApiModelProperty(value = "客服申请日志")
    private TicketsFormLogVo customerServiceTicketsFormLogVo;

    @ApiModelProperty(value = "财务申请日志")
    private TicketsFormLogVo financialTicketsFormLogVo;

}
