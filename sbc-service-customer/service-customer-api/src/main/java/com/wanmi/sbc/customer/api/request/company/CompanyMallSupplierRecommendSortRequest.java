package com.wanmi.sbc.customer.api.request.company;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: sbc-backgroud
 * @description: aa
 * @author: gdq
 * @create: 2023-06-13 15:24
 **/
@ApiModel
@Data
public class CompanyMallSupplierRecommendSortRequest implements Serializable {
    private static final long serialVersionUID = 7552570854904824427L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "排序")
    private BigDecimal sort;
}

