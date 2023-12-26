package com.wanmi.sbc.account.wallet.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 线下账户entity
 * Created by sunkun on 2017/11/30.
 */
@Data
@Entity
@Table(name = "customer_wallet")
public class CustomerWallet implements Serializable {

    /**
     * 钱包id
     */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "wallet_id")
    private Long walletId;

    /**
     * 客户id
     */
    @Column(name = "customer_id")
    private String customerId;

    /**
     * 客户账户
     */
    @Column(name = "customer_account")
    private String customerAccount;

    /**
     * 客户名称
     */
    @Column(name = "customer_name")
    private String customerName;

    /**
     * 可用余额【充值金额+赠送金额】
     */
    @Column(name = "balance")
    private BigDecimal balance;

    /**
     * 充值金额
     */
    @Column(name = "recharge_balance")
    private BigDecimal rechargeBalance;

    /**
     * 赠送金额
     */
    @Column(name = "give_balance")
    private BigDecimal giveBalance;

    /**
     * 提现余额
     * */
    @Column(name = "withdrawal_balanace")
    private BigDecimal withDrawalBalance;

    /**
     * 冻结余额（提现中）
     */
    @Column(name = "block_balance")
    private BigDecimal blockBalance;

    /**
     * 账户状态（1可用，2冻结）
     */
    @Column(name = "customer_status")
    @Enumerated
    private DefaultFlag customerStatus;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    @Column(name = "create_id")
    private String createId;

    /**
     * 修改时间
     */
    @Column(name = "update_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

    /**
     * 修改人
     */
    @Column(name = "update_id")
    private String updateId;

    /**
     * 删除标识
     */
    @Column(name = "del_flag")
    @Enumerated
    private DefaultFlag delFlag;

    /**
     * 删除时间
     */
    @Column(name = "del_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime delTime;

    /**
     * 冻结金额状态 0 可用 1 不可用
     */
    @Column(name = "give_balance_state")
    private  Integer giveBalanceState;

    @Column(name = "store_id")
    private Long storeId;

    @Column(name = "is_enable")
    private Integer isEnable;

}
