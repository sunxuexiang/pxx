package com.wanmi.sbc.customer.model.root;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 会员资金统计映射实体对象
 * @author: Geek Wang
 * @createDate: 2019/2/19 9:42
 * @version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerBase implements Serializable{

    /**
     * 会员ID
     */
    private String customerId;

    /**
     * 账户
     */
    private String customerAccount;

    /**
     * 会员名称
     */
    private String customerName;

    /**
     * 会员等级ID
     */
    private Long customerLevelId;

    /**
     * 会员等级名称
     */
    private String customerLevelName;

    public CustomerBase(String customerId, String customerAccount) {
        this.customerId = customerId;
        this.customerAccount = customerAccount;
    }

    public CustomerBase(String customerId, Long customerLevelId) {
        this.customerId = customerId;
        this.customerLevelId = customerLevelId;
    }
}
