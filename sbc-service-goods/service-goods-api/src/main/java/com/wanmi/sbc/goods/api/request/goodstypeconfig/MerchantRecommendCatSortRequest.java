package com.wanmi.sbc.goods.api.request.goodstypeconfig;

import io.swagger.annotations.ApiModel;
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
public class MerchantRecommendCatSortRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NonNull
    private String merchantTypeId;

    @NonNull
    private Integer sort;

}