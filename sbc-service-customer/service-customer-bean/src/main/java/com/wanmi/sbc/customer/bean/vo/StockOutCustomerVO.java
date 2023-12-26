package com.wanmi.sbc.customer.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: StockOutCustomerVO
 * @Description: TODO
 * @Date: 2020/8/19 19:24
 * @Version: 1.0
 */
@ApiModel
@Data
public class StockOutCustomerVO implements Serializable {

    private static final long serialVersionUID = -7683934939836693129L;
    /**
     * 客户ID
     */
    @ApiModelProperty(value = "客户ID")
    private String customerId;

    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @ApiModelProperty(value = "业务员名称")
    private String employeeName;

    @ApiModelProperty(value = "地址")
    private String address;
}
