package com.wanmi.sbc.goods.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

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
@ApiModel(value = "商家搜索分类结果")
public class GoodsMallPlatformSupplierTabSearchResponse implements Serializable {

    private static final long serialVersionUID = 4239429839327965050L;

    @ApiModelProperty(value = "商城分类列表")
    private List<MallTab> mallTabs;

    @ApiModelProperty(value = "商家平铺")
    private List<CompanySupplier> suppliers;

    @Data
    public static class CompanySupplier implements Serializable {
        @ApiModelProperty(value = "商家Id")
        private Long companyInfoId;
        @ApiModelProperty(value = "店铺名称")
        private String storeName;
        @ApiModelProperty(value = "店铺Id")
        private Long storeId;
        @ApiModelProperty(value = "店铺图片")
        private String storeLogo;
        @ApiModelProperty(value = "联系电话")
        private String contactPhone;

        @ApiModelProperty(value = "店铺对应的商城")
        private Set<Long> tabIds;
    }

    @Data
    public static class MallTab implements Serializable {
        @ApiModelProperty(value = "商城分类Id")
        private Long tabId;

        @ApiModelProperty(value = "商城分类名称")
        private String tabName;

        @ApiModelProperty(value = "商城商家列表")
        private List<CompanySupplier> companySuppliers;
    }
}

