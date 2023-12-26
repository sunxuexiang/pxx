package com.wanmi.sbc.customer.api.request.store;


import com.wanmi.sbc.customer.api.request.CustomerBaseRequest;
import com.wanmi.sbc.customer.bean.enums.PileState;
import com.wanmi.sbc.customer.bean.enums.PresellState;
import com.wanmi.sbc.wallet.bean.enums.JingBiState;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商家预售状态
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
public class StorePresellRequest extends CustomerBaseRequest {


    @ApiModelProperty(value = "店铺id")
    private Long storeId;

    @ApiModelProperty(value = "预售状态")
    private PresellState presellState;
}
