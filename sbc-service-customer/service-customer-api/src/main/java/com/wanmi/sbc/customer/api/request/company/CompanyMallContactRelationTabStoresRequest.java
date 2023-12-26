package com.wanmi.sbc.customer.api.request.company;

import com.wanmi.sbc.customer.bean.vo.CompanyMallContractRelationVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
public class CompanyMallContactRelationTabStoresRequest implements Serializable {

    private Long tabId;
}
