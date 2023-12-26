package com.wanmi.sbc.customer.api.request.company;

import com.wanmi.sbc.customer.bean.vo.CompanyMallContractRelationVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-06-15 10:42
 **/
@ApiModel(description = "签约属性关联")
@Data
public class CompanyMallContactRelationBatchSaveRequest implements Serializable {

    @ApiModelProperty(value = "签约属性列表")
    private List<CompanyMallContractRelationVO> contactRelationList;

    @ApiModelProperty(value = "1:tab ,2:商城")
    @NotNull
    private Integer relationType;

    @ApiModelProperty(value = "对应的公司Id")
    @NotNull
    private Long storeId;
}
