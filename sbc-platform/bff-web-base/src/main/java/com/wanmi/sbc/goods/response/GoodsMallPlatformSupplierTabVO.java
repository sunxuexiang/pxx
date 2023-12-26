package com.wanmi.sbc.goods.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-07-12 11:38
 **/
@Data
public class GoodsMallPlatformSupplierTabVO implements Serializable {
    private static final long serialVersionUID = 4239429839327965050L;

    @ApiModelProperty(value = "商城分类Id")
    private Long tabId;

    @ApiModelProperty(value = "商城分类名称")
    private String tabName;

    @ApiModelProperty(value = "商城商家列表")
    private List<GoodsMallPlatformSupplierVO> companySuppliers;
}
