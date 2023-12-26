package com.wanmi.sbc.shopcart.api.request.purchase;


import com.wanmi.sbc.shopcart.bean.dto.PurchaseSaveDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * <p></p>
 * author: sunkun
 * Date: 2018-11-30
 */
@Data
@ApiModel
public class BatchAddCartGoodsRequest extends PurchaseSaveDTO {

    @ApiModelProperty(value = "批量加购的商品ID")
    private List<String> orderIds;

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
