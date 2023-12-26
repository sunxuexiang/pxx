package com.wanmi.sbc.wallet.wallet.model.root;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "customer_bind_bank_card")
public class CustomerBindBankCard implements Serializable {
    private static final long serialVersionUID = 6020617558813768400L;

    @Id
    @Column(name = "bank_card_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bankCardId;

    /**
     * 钱包id
     */
    @Column(name = "wallet_id")
    private Long walletId;

    /**
     * 开户行
     */
    @Column(name = "bank_name")
    private String bankName;

    /**
     * 开户支行
     */
    @Column(name = "bank_branch")
    private String bankBranch;

    /**
     * 持卡人
     */
    @Column(name = "card_holder")
    private String cardHolder;

    /**
     * 银行卡类型
     */
    @Column(name = "card_type")
    private String cardType;

    /**
     * 银行卡号
     */
    @Column(name = "bank_code")
    private String bankCode;

    /**
     * 绑定手机号
     */
    @Column(name = "bind_phone")
    private String bindPhone;

    /**
     * 预留字段（开户地区）【存省市区id，以,分割】
     */
    @Column(name = "bank_area")
    private String bankArea;

    /**
     * 绑定时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "bind_time")
    private LocalDateTime bindTime;

    /**
     * 创建时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 删除标识
     */
    @Column(name = "del_flag")
    @Enumerated
    private DefaultFlag delFlag;

    /**
     * 删除时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "del_time")
    private LocalDateTime delTime;

}
