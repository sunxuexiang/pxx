package com.wanmi.sbc.goods.api.request.goodstypeconfig;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

/**
 * <p>排序修改</p>
 *
 * @author sgy
 * @date 2023-06-08 10:53:36
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantRecommendGoodsSortRequest implements Serializable  {
    private static final long serialVersionUID = 1L;

    @NonNull
    private String merchantRecommendId;

    @NonNull
    private Integer sort;
    @NonNull
    @ApiModelProperty(value = "店铺Id")
    private Long storeId;
    @NonNull
    @ApiModelProperty(value = "商家Id")
    private Long companyInfoId;

}