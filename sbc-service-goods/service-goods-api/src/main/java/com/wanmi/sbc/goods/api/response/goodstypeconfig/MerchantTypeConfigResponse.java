package com.wanmi.sbc.goods.api.response.goodstypeconfig;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>分类推荐分类列表结果</p>
 * @author sgy
 * @date 2023-06-07 10:53:36
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantTypeConfigResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 分类推荐分类列表结果
     */
    @ApiModelProperty(value = "分类推荐分类列表结果")
    private List<String> merchantTypeConfigIds;
    @ApiModelProperty(value = "店铺Id")
    private Long storeId;


    @ApiModelProperty(value = "商家id")
    private Long companyInfoId;
}
