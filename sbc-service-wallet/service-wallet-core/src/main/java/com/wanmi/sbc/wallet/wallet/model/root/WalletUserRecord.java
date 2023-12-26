package com.wanmi.sbc.wallet.wallet.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.wallet.bean.enums.BudgetType;
import com.wanmi.sbc.wallet.bean.enums.TradeStateEnum;
import com.wanmi.sbc.wallet.bean.enums.WalletRecordTradeType;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "wallet_user_record")
public class WalletUserRecord implements Serializable {
    private static final long serialVersionUID = 8386444122907077089L;

    /**
     * 交易单号
     */
    @Id
    @Column(name = "user_wallet_id")
    private String userWalletId;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "store_id")
    private String storeId;

    @Column(name = "give_balance")
    private BigDecimal giveBalance;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "consumption_balance")
    private BigDecimal consumptionBalance;


}
