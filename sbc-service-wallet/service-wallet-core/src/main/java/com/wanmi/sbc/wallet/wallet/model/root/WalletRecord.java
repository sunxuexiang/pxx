package com.wanmi.sbc.wallet.wallet.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.wallet.bean.enums.BudgetType;
import com.wanmi.sbc.wallet.bean.enums.TradeStateEnum;
import com.wanmi.sbc.wallet.bean.enums.WalletRecordTradeType;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "wallet_record")
public class WalletRecord implements Serializable {
    private static final long serialVersionUID = 8386444122907077089L;

    /**
     * 交易单号
     */
    @Id
    @Column(name = "record_no")
    private String recordNo;

    /**
     * 交易备注
     */
    @Column(name = "trade_remark")
    private String tradeRemark;

    /**
     * 客户账号
     */
    @Column(name = "customer_account")
    private String customerAccount;

    /**
     * 关联订单号
     */
    @Column(name = "relation_order_id")
    private String relationOrderId;

    /**
     * 虚拟商品id
     */
    @Column(name = "virtual_goods_id")
    private Integer virtualGoodsId;

    /**
     * 交易类型【0充值，1提现，2余额支付，3购物返现】
     */
    @Column(name = "trade_type")
    @Enumerated
    private WalletRecordTradeType tradeType;

    /**
     * 收支类型
     * 收支类型 0收入，1支出
     */
    @Column(name = "budget_type")
    @Enumerated
    private BudgetType budgetType;

    /**
     * 交易金额
     */
    @Column(name = "deal_price")
    private BigDecimal dealPrice;

    /**
     * 手续费
     */
    @Column(name = "charge_price")
    private BigDecimal chargePrice;

    /**
     * 提现方式
     */
    @Column(name = "extract_type")
    private String extractType;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

    /**
     * 交易时间
     */
    @Column(name = "deal_time")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime dealTime;

    /**
     * 当前余额
     */
    @Column(name = "current_balance")
    private BigDecimal currentBalance;

    /**
     * 交易状态 0 未支付 1 待确认 2 已支付
     */
    @Column(name = "trade_state")
    @Enumerated
    private TradeStateEnum tradeState;

    @Column(name = "pay_type")
    private Integer payType;

    /**
     * 冻结的赠送金额
     */
    @Column(name = "block_give_balance")
    private BigDecimal blockGiveBalance;

    /**
     * 活动类型
     */
    @Column(name = "activity_type")
    private String activityType;

    @Column(name = "store_id")
    private String storeId;

}
