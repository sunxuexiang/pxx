package com.wanmi.sbc.customer.api.request.company;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: sbc-backgroud
 * @description: aa
 * @author: gdq
 * @create: 2023-06-13 15:24
 **/
@ApiModel
@Data
public class CompanyMallSupplierRecommendBatchAddRequest implements Serializable{
    private static final long serialVersionUID = 7552570854904824427L;

    @ApiModelProperty(value = "公司IDS")
    private List<Long> companyInfoIds;

    @ApiModelProperty(value = "操作人")
    private String operator;
}

