package com.wanmi.sbc.order.api.request.purchase;

import com.wanmi.sbc.order.bean.dto.PurchaseSaveDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p></p>
 * author: sunkun
 * Date: 2018-11-30
 */
@Data
@ApiModel
public class PurchaseUpdateNumRequest extends PurchaseSaveDTO {

    private static final long serialVersionUID = 3517002784178075668L;
    @ApiModelProperty(value = "营销号")
    private Long marketingid;


    @ApiModelProperty(value = "是否需要后端查询营销号")
    private Boolean needCheack = false;

    @ApiModelProperty(value = "店铺ID")
    private Long storeId;

    @ApiModelProperty(value = "是否预售")
    private boolean presellFlag;
}
