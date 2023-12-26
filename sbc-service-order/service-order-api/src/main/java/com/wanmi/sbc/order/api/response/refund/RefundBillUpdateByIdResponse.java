package com.wanmi.sbc.order.api.response.refund;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @description  退款流水
 * @author  shiy
 * @date    2023/3/13 16:42
 * @params  
 * @return  
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class RefundBillUpdateByIdResponse implements Serializable{

    private static final long serialVersionUID = 8530724458639491058L;

    @ApiModelProperty(value = "修改结果")
    private Integer result;
}
