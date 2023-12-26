package com.wanmi.sbc.shopcart.api.request.purchase;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class ImportPurchaseMarketingRequest implements Serializable {


    @NotNull(message = "用户不能为空!")
    @ApiModelProperty(value = "用户id")
    private String customerId;

    @ApiModelProperty(value = "前端采购单勾选的skuIdList")
    private List<String> goodsInfoIds;

    @ApiModelProperty(value = "仓库Id")
    private Long wareId;

    @ApiModelProperty(value = "下单商品")
    private List<ImportGoodsInfos> importGoodsInfosList;

}
