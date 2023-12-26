package com.wanmi.sbc.goods.api.request.goods;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * com.wanmi.sbc.goods.api.request.goods.GoodsModifySeqNumRequest
 * 修改商品排序序号
 * @author lf
 * @dateTime 2020/12/30
 */
@ApiModel
@Data
public class GoodsModifySeqNumRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品编号，采用UUID
     */
    @ApiModelProperty(value = "商品编号，采用UUID")
    private String goodsId;

    /**
     * 商品排序序号
     */
    @ApiModelProperty(value = "商品排序序号")
    private Integer goodsSeqNum;
    
    /**
     * 店铺Id，不为null表示店铺内排序
     */
    @ApiModelProperty(value = "店铺Id")
    private Long storeId;
}
