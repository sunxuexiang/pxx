package com.wanmi.sbc.shopcart.api.request.purchase;

import com.wanmi.sbc.shopcart.bean.dto.PurchaseSaveDTO;
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
public class PurchaseBatchSaveRequest extends PurchaseSaveDTO {

    private static final long serialVersionUID = -3559582099527002037L;


    @ApiModelProperty(value = "营销id")
    private Long marketingId;


    @ApiModelProperty(value = "市")
    private Long cityId;
    /**
     * 省
     */
    @ApiModelProperty(value = "省")
    private Long provinceId;

    @ApiModelProperty(value = "是否是强制修改")
    private Boolean forceUpdate=false;


}
