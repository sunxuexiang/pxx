package com.wanmi.sbc.customer.api.request.company;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.XssUtils;
import com.wanmi.sbc.customer.bean.vo.CompanyMallSupplierTabVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: sbc-backgroud
 * @description: aa
 * @author: gdq
 * @create: 2023-06-13 15:24
 **/
@ApiModel
@Data
public class CompanyMallSupplierTabPageRequest extends BaseQueryRequest {
    private static final long serialVersionUID = 7552570854904824427L;

    @ApiModelProperty(value = "商家商城Id")
    private Long id;


    @ApiModelProperty(value = "idList")
    private List<Long> idList;

    @ApiModelProperty(value = "商家商城分类")
    private String name;

    /**
     * 删除标志
     */
    @ApiModelProperty(value = "删除标志")
    private DeleteFlag deleteFlag;

    @ApiModelProperty(value = "商家商城分类")
    private List<Long> Ids;

    private Integer openStatus;

}

