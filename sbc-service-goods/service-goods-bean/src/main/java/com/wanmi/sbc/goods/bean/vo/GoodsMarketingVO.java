package com.wanmi.sbc.goods.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class GoodsMarketingVO implements Serializable {

    private static final long serialVersionUID = 1466292947486301520L;

    /**
     * 唯一编号
     */
    @ApiModelProperty(value = "唯一编号")
    private String id;

    /**
     * sku编号
     */
    @ApiModelProperty(value = "sku编号")
    private String goodsInfoId;

    /**
     * 客户编号
     */
    @ApiModelProperty(value = "客户编号")
    private String customerId;

    /**
     * 营销编号
     */
    @ApiModelProperty(value = "营销编号")
    private Long marketingId;

    /**
     * 是否是快照
     */
    @ApiModelProperty(value = "是否是快照读")
    private Boolean isSnapshot = false;
}
