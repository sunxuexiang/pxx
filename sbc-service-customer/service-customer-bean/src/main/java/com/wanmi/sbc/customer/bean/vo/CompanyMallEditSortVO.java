package com.wanmi.sbc.customer.bean.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: sbc-backgroud
 * @description: 商家入驻，批发市场VO
 * @author: gdq
 * @create: 2023-06-13 14:51
 **/
@Data
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class CompanyMallEditSortVO implements Serializable {
    private static final long serialVersionUID = 4860094582115362527L;

    private Long id;

    private BigDecimal sort;
}
