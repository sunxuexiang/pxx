package com.wanmi.sbc.goods.request;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class GoodsBrandsQueryByStoreCatIdAndStoreRequest extends BaseQueryRequest {


    @ApiModelProperty(value = "商城类目Id")
    private Long storeCatId;

    @ApiModelProperty(value = "商城Id")
    private Long storeId;
}
