package com.wanmi.sbc.customer.parentcustomerrela.PK;


import lombok.Data;

import java.io.Serializable;

/**
 * @author baijianzhong
 * @ClassName ParentCustomerRela
 * @Date 2020-05-26 15:31
 * @Description TODO
 **/
@Data
public class ParentCustomerRelaPK implements Serializable {

    /**
     * 主账号Id
     */
    private String parentId;

    /**
     * 子账号Id
     */
    private String customerId;

}
