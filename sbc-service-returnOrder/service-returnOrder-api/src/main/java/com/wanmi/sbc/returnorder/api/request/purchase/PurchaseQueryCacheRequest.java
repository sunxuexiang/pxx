package com.wanmi.sbc.returnorder.api.request.purchase;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * <p></p>
 * author: sunkun
 * Date: 2018-12-03
 */
@Data
@ApiModel
public class PurchaseQueryCacheRequest implements Serializable {

    private static final long serialVersionUID = 6498910185574338392L;

    @ApiModelProperty(value = "客户id")
    @NotBlank
    private String customerId;

    @ApiModelProperty(value = "商品ids")
    @Size(min = 1)
    private List<Long> devaningId;

    @ApiModelProperty(value = "商品ids")
    private List<String> goodsInfos;


    /**
     * 商品信息id
     */
    @ApiModelProperty(value = "商品id")
    private String skuId;

    /**
     * 商品信息id
     */
    @ApiModelProperty(value = "仓库id")
    private Long wareId;

}
