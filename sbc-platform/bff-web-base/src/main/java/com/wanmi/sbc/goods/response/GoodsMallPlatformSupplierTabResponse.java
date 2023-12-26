package com.wanmi.sbc.goods.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnegative;
import java.io.Serializable;
import java.util.List;

/**
 * @program: sbc-backgroud
 * @description: 商家市场列表
 * @author: gdq
 * @create: 2023-06-16 08:57
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "商家分类结果")
public class GoodsMallPlatformSupplierTabResponse implements Serializable {

    private static final long serialVersionUID = 4239429839327965050L;

    @ApiModelProperty(value = "商城分类列表")
    private List<GoodsMallPlatformSupplierTabVO> mallTabs;

    @ApiModelProperty(value = "商家平铺-全部")
    private List<GoodsMallPlatformSupplierVO> suppliers;
}

