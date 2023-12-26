package com.wanmi.sbc.customer.api.request.company;

import com.wanmi.sbc.common.enums.DeleteFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel
@Data
public class CompanyInfoQueryByIdsRequest {

    @ApiModelProperty(value = "公司信息IDs")
    @NotEmpty
    private List<Long> companyInfoIds;

    @ApiModelProperty(value = "是否删除")
    @NotNull
    private DeleteFlag deleteFlag;
}
