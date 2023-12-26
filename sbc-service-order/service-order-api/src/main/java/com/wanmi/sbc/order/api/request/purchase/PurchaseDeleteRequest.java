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
public class PurchaseDeleteRequest extends PurchaseSaveDTO {

    private static final long serialVersionUID = -4861734589820460379L;

    /**
     * 用户id,仅限后台代客下单
     */
    @ApiModelProperty(value = "用户id,仅限后台代客下单")
    private String customerId;

}
