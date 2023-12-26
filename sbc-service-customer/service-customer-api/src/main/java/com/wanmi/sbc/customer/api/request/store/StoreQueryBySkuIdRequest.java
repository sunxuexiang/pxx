package com.wanmi.sbc.customer.api.request.store;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * 根据skuid查storeid
 * Created by CHENLI on 2017/4/13.
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreQueryBySkuIdRequest extends BaseQueryRequest {

    private static final long serialVersionUID = 8013401509347978925L;


    /**
     * skuId
     */
    @ApiModelProperty(value = "skuId")
    private String skuId;



}
