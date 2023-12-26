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
public class CompanyMallContractRelationPageRequest extends BaseQueryRequest {
    private static final long serialVersionUID = 7552570854904824427L;

    @ApiModelProperty(value = "签约类型")
    private Integer relationType;

    @ApiModelProperty(value = "公司Id")
    private Long companyInfoId;

    @ApiModelProperty(value = "公司Id")
    private List<Long> companyInfoIds;

    @ApiModelProperty(value = "店铺Id")
    private Long storeId;

    @ApiModelProperty(value = "店铺Ids")
    private List<Long> storeIds;

    /**
     * 删除标志
     */
    @ApiModelProperty(value = "删除标志")
    private DeleteFlag deleteFlag;

    @ApiModelProperty(value = "关联的值")
    private String relationValue;

}

