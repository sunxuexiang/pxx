package com.wanmi.sbc.customer.api.request.company;

import com.wanmi.sbc.customer.bean.vo.CompanyMallSupplierRecommendVO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @program: sbc-backgroud
 * @description: aa
 * @author: gdq
 * @create: 2023-06-13 15:24
 **/
@ApiModel
@Data
public class CompanyMallSupplierRecommendAddRequest extends CompanyMallSupplierRecommendVO {
    private static final long serialVersionUID = 7552570854904824427L;

    private Long marketId;
}

