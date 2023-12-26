package com.wanmi.sbc.live.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
/**
 * <p>直播商品VO</p>
 */
@ApiModel
@Data
public class LiveStreamGoodsVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 直播id
     */
    @ApiModelProperty(value = "直播id")
    private Integer liveId;
    /**
     * 商品SPU_ID
     */
    @ApiModelProperty(value = "商品SPU_ID")
    private String goodsId;
    /**
     * 直播商品SKU_ID
     */
    @ApiModelProperty(value = "直播商品SKU_ID")
    private String goodsInfoId;

    /**
     * 0 批发 1散批
     */
    private Long goodsType;

    /**
     * 商品直播间上下架  0 下架 1上架
     */
    private Long goodsStatus;

    /**
     * 仓库id
     */
    @ApiModelProperty(value = "仓库id")
    private Long wareId;

    /**
     * 讲解标识,0:未讲解1:讲解中
     */
    @ApiModelProperty(value = "讲解标识,0:未讲解1:讲解中")
    private Integer explainFlag;

    /**
     * 店铺ID
     */
    @ApiModelProperty(value = "店铺ID")
    private Long storeId;
}
