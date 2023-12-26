package com.wanmi.sbc.pay.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.EnableStatus;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author lm
 * @date 2022/10/21 9:25
 */
@Data
@Entity
@Table(name = "pay_withdraw")
public class PayWithDraw {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "withdraw_id")
    private Integer withdrawId;

    /**
     * 账号名称
     */
    @Column(name = "account_name")
    private String accountName;

    /**
     * 账号
     */
    @Column(name = "bank_account")
    private String bankAccount;

    /**
     * 开户行名称
     */
    @Column(name = "bank_name")
    private String bankName;

    /**
     * 开户行联行号
     */
    @Column(name = "bank_no")
    private String bankNo;

    /**
     * 是否启用
     */
    @Column(name = "enable_status")
    @Enumerated
    private EnableStatus enableStatus;

    @Column(name = "del_flag")
    @Enumerated
    private DeleteFlag delFlag;

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
}
