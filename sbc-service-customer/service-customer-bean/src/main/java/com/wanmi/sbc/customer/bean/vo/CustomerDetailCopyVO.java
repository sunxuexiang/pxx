package com.wanmi.sbc.customer.bean.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lm
 * @date 2022/09/20 20:33
 */
@Data
public class CustomerDetailCopyVO implements Serializable {

    /*客户ID*/
    private String customerId;
    /*客户姓名*/
    private String customerName;
    /*客户省ID*/
    private String provinceId;
    /*客户市*/
    private String cityId;
    /*客户区域*/
    private String areaId;
    /*客户联系人姓名*/
    private String contactName;
    /*客户联系人手机*/
    private String contactPhone;
}
