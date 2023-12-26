package com.wanmi.sbc.customer.api.request.account;

import lombok.Data;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-07-18 10:55
 **/
@Data
public class ResetAccountPasswordRequest {
    private String account;
    private String password;
    // 0: 客户， 1：boss， 2：supplier
    private Integer accountType;
}
