package com.wanmi.sbc.customer.bean.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: sbc_h_tian
 * @description: 批量绑定子账户
 * @author: Mr.Tian
 * @create: 2020-06-03 14:49
 **/
@ApiModel
@Data
public class CustomerAddRelaVO implements Serializable {

    private static final long serialVersionUID = 7827563638746345276L;
    /**
     * 是否成功绑定成为子账户
     */
    private Boolean flag;
    /**
     * 被绑定手机号码
     */
    private String phone;

    //客户id
    private String customerId;
}
