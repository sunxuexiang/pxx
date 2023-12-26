package com.wanmi.sbc.order.bean.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @desc  
 * @author shiy  2023/12/14 12:28
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class FreightTempDescQueryDTO implements Serializable{
    private String deliveryAddressId;
    private Integer deliveryWay;
}
