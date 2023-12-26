package com.wanmi.sbc.customer.api.response.store;

import com.wanmi.sbc.customer.api.vo.StoreBaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreQueryByUserIdResponse implements Serializable {

    private static final long serialVersionUID = 8729267120923744281L;

    /**
     * 店铺信息
     */
    @ApiModelProperty(value = "店铺信息")
    private StoreBaseVO storeVO;
}