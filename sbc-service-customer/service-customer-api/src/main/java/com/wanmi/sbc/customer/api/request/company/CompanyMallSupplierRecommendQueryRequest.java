package com.wanmi.sbc.customer.api.request.company;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.customer.bean.vo.CompanyMallSupplierRecommendVO;
import com.wanmi.sbc.customer.bean.vo.CompanyMallSupplierTabVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @program: sbc-backgroud
 * @description: aa
 * @author: gdq
 * @create: 2023-06-13 15:24
 **/
@ApiModel
@Data
public class CompanyMallSupplierRecommendQueryRequest extends CompanyMallSupplierRecommendVO {
    private static final long serialVersionUID = 7552570854904824427L;

    @ApiModelProperty(value = "商家推荐Id")
    private Long id;

    /**
     * 数据是否用
     */
    @ApiModelProperty(value = "数据是否用")
    private DeleteFlag deleteFlag;
}

