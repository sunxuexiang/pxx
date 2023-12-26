package com.wanmi.sbc.customer.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: sbc-backgroud
 * @description: 商家入驻，批发市场VO
 * @author: gdq
 * @create: 2023-06-13 14:51
 **/
@Data
@ApiModel
public class CompanyMallBulkMarketProvinceVO implements Serializable {
    private static final long serialVersionUID = 4860094582115362527L;

    @ApiModelProperty(value = "省")
    private Long provinceId;


    @ApiModelProperty(value = "省名称")
    private String provinceName;


    private List<CompanyMallBulkMarketVO> markets;

}
