package com.wanmi.sbc.customer.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: Geek Wang
 * @createDate: 2019/2/19 9:42
 * @version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerMergeVO implements Serializable{

    /**
     * 会员ID
     */
    private String customerId;

    private String failingPhone;
}
