package com.wanmi.sbc.goods.api.request.info;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 根据商品SKU编号查询请求
 * Created by daiyitian on 2017/3/24.
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsInfoByIdRequest implements Serializable {

    private static final long serialVersionUID = 8415135684020619843L;

    /**
     * SKU编号
     */
    @ApiModelProperty(value = "SKU编号")
    private String goodsInfoId;

    @ApiModelProperty(value = "店铺Id")
    private Long storeId;

    /**
     * 匹配的分仓Id
     */
    @ApiModelProperty(value = "分仓Id")
    private Long wareId;

    /**
     * 是否能匹配到仓
     */
    @ApiModelProperty(value = "是否能匹配到仓")
    private Boolean matchWareHouseFlag;


    /**
     * SKU编号
     */
    @ApiModelProperty(value = "父级SKU编号")
    private String parentGoodsInfoId;
}
