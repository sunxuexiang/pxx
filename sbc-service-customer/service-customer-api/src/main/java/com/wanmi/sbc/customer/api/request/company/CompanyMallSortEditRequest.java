package com.wanmi.sbc.customer.api.request.company;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @program: sbc-backgroud
 * @description: aa
 * @author: gdq
 * @create: 2023-06-13 15:24
 **/
@ApiModel
@Data
public class CompanyMallSortEditRequest implements Serializable {

    @ApiModelProperty(value = "1:市场，2：商城，3:推荐商家")
    private Integer type;

    private Long id;

    private BigDecimal sort;
}

