package com.wanmi.sbc.customer.api.request.company;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;


/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-06-15 10:42
 **/
@ApiModel(description = "签约属性关联")
@Data
public class CompanyMallContactRelationMarketStoresRequest implements Serializable {

    private Long marketId;
}
