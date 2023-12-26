package com.wanmi.sbc.customer.api.request.company;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DeleteFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

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
public class CompanyMallBulkMarketPageRequest extends BaseQueryRequest {

    /**
     * 市场IDS
     */
    @ApiModelProperty(value = "市场IDS")
    private List<Long> marketIds;

    /**
     * 市场名称模糊查找
     */
    @ApiModelProperty(value = "市场名称模糊查找")
    private String marketName;

    /**
    * 数据是否用
    */
    @ApiModelProperty(value = "数据是否用")
    private DeleteFlag deleteFlag;

    @ApiModelProperty(value = "1:打开，0：关闭")
    private Integer openStatus;

    private String concatInfo;

    private Integer provinceId;


}

