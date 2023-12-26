package com.wanmi.sbc.customer.netWorkService.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RetrunLocationJWBeanVO implements Serializable {
    private static final long serialVersionUID = -1755220444491292875L;
    private BigDecimal lat;//纬度值
    private BigDecimal lng;//经度值
}
