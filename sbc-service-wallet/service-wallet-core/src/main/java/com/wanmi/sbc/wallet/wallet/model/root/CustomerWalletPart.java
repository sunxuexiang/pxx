package com.wanmi.sbc.wallet.wallet.model.root;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerWalletPart implements Serializable {


    private String customerId;

    private String customerAccount;

    private String customerName;

    private BigDecimal balance;

    private LocalDateTime applyTime;

}
