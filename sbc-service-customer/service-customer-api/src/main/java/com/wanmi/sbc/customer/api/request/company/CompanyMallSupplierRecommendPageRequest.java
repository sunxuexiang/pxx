package com.wanmi.sbc.customer.api.request.company;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DeleteFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @program: sbc-backgroud
 * @description: aa
 * @author: gdq
 * @create: 2023-06-13 15:24
 **/
@ApiModel
@Data
public class CompanyMallSupplierRecommendPageRequest extends BaseQueryRequest {
    private static final long serialVersionUID = 7552570854904824427L;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "商家名字")
    private String companyInfoName;

    /**
     * 数据是否用
     */
    @ApiModelProperty(value = "删除标识")
    private DeleteFlag deleteFlag;

    @ApiModelProperty(value = "公司IDs")
    private List<Long> companyIds;

    @ApiModelProperty(value = "公司IDs")
    private List<Long> storeIds;

    // 1:设置过的，0：未设置，-1 全部
    private Integer assignSort;

    // 市场id
    private Long marketId;
}

