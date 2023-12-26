package com.wanmi.sbc.wallet.api.request.wallet;

import com.wanmi.sbc.wallet.api.request.BalanceBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketsFormModifyRequest extends BalanceBaseRequest {
    private static final long serialVersionUID = 2321329611892557532L;

    @ApiModelProperty(value = "工单id")
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

    /**
     * 工单类型【1充值，2提现】
     */
    @ApiModelProperty(value = "工单类型【1充值，2提现】")
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
     * 审核时间
     */
    @ApiModelProperty(value = "审核时间")
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

    @ApiModelProperty(value = "客户姓名")
    private String customerName;

    @ApiModelProperty(value = "客户电话")
    private String customerPhone;
 }
