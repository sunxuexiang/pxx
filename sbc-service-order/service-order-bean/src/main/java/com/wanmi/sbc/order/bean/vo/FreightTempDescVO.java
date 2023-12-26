package com.wanmi.sbc.order.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
public class FreightTempDescVO implements Serializable{
    private String freightTempDesc;
    private Integer leaseSeconds=10;
}
