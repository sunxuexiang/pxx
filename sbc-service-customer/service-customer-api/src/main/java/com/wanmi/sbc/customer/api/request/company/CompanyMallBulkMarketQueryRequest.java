package com.wanmi.sbc.customer.api.request.company;

import com.wanmi.sbc.common.enums.DeleteFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: sbc-backgroud
 * @description: aa
 * @author: gdq
 * @create: 2023-06-13 15:24
 **/
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyMallBulkMarketQueryRequest {

    @ApiModelProperty(value = "市场Id")
    private Long marketId;

    /**
     * 数据是否用
     */
    @ApiModelProperty(value = "删除标志")
    private DeleteFlag deleteFlag;
}

