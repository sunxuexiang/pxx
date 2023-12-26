package com.wanmi.sbc.goods.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 店铺下的商品SKU实体对象
 */
@ApiModel
@Data
@Builder
public class GoodsStoreGroupVO implements Serializable {

    /**
     * 返回
     */
    @ApiModelProperty(value = "我的囤货商品")
    private List<GoodsInfoVO> goodsInfos;

    /**
     * 店铺id
     */
    @ApiModelProperty(value = "店铺id")
    private Long storeId;
    /**
     * 店铺名称
     */
    @ApiModelProperty(value = "店铺名称")
    private String storeName;

    /**
     * 店铺类型
     */
    @ApiModelProperty(value = "店铺类型")
    private Integer companyType;
}
