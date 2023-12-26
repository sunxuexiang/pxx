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
public class CompanyMallBatchSortEditRequest implements Serializable {


    @ApiModelProperty(value = "排序对象")
    private List<Sort> sorts;

    @ApiModelProperty(value = "1:市场，2：商城，3:推荐商家")
    private Integer type;

    @Data
    public static class Sort implements Serializable {

        private static final long serialVersionUID = -2950427092338757154L;

        @ApiModelProperty(value = "排序主键Id")
        private Long sortId;

        @ApiModelProperty(value = "排序对应的序号")
        private BigDecimal sort;
    }
}

