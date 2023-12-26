package com.wanmi.sbc.customer.api.request.company;

import com.wanmi.sbc.customer.api.request.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel
@Data
public class CompanyMallContractRelationPageBaseRequest extends BaseQueryRequest {
        static final long serialVersionUID = 7552570854904824427L;

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

        @ApiModelProperty(value = "关联的值")
        private String relationValue;
}