package com.wanmi.sbc.customer.api.request.store;


import com.wanmi.sbc.customer.api.request.CustomerBaseRequest;
import com.wanmi.sbc.customer.bean.enums.ExportState;
import com.wanmi.sbc.customer.bean.enums.PileState;
import com.wanmi.sbc.customer.bean.enums.PresellState;
import com.wanmi.sbc.wallet.bean.enums.JingBiState;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 商家囤货状态
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
public class StorePlieRequest extends CustomerBaseRequest {


    @ApiModelProperty(value = "店铺id")
    private Long storeId;

    @ApiModelProperty(value = "囤货状态")
    private PileState pileState;

    @ApiModelProperty(value = "鲸币权限状态")
    private JingBiState jingBiState;
    @ApiModelProperty(value = "商家配送方式")
    private List<StoreDeliveryAreaRequest> storeDeliveryAreaRequests;
    @ApiModelProperty(value = "预售状态")
    private PresellState presellState;

    @ApiModelProperty(value = "商品导出状态")
    private ExportState exportState;

}
