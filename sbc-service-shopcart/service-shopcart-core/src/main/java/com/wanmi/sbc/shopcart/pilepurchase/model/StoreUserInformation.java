package com.wanmi.sbc.shopcart.pilepurchase.model;

import lombok.*;

import java.math.BigInteger;

/**
 * 描述
 *
 * @author yitang
 * @version 1.0
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreUserInformation {
    /**
     * 客户编号
     */
    private String customerId;

    /**
     * 采购单编号
     */
    private Long purchaseId;

    public static StoreUserInformation convertFromNativeSQLResult(Object result) {
        StoreUserInformation response = new StoreUserInformation();
        response.setCustomerId((String) ((Object[]) result)[0]);
        response.setPurchaseId(((BigInteger) ((Object[]) result)[1]).longValue());
        return response;
    }
}
