package com.wanmi.sbc.goods.api.request.marketing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>商品营销</p>
 * author: sunkun
 * Date: 2018-11-02
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoodsMarketingListByCustomerIdAndGoodsInfoIdRequest implements Serializable {


    private static final long serialVersionUID = -5008660567512415791L;
    /**
     * 用户编号
     */
    @ApiModelProperty(value = "用户编号")
    @NotBlank
    private String customerId;

    /**
     * 用户编号
     */
    @ApiModelProperty(value = "商品id")
    @NotBlank
    private String goodsInfoIds;

}
