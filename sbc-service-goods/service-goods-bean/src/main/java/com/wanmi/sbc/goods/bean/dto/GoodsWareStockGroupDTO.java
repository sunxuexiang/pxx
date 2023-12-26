package com.wanmi.sbc.goods.bean.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>sku分仓库存表列表结果</p>
 *
 * @author zhangwenchang
 * @date 2020-04-06 17:22:56
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsWareStockGroupDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * sku ID
     */
    @ApiModelProperty(value = "sku ID")
    private String goodsInfoId;

    /**
     * sku 编号
     */
    @ApiModelProperty(value = "sku 编号")
    private String goodsInfoNo;

    /**
     * 仓库库存
     */
    @ApiModelProperty(value = "仓库库存")
    private List<GoodsWareStockDTO> goodsWareStockVOList;
}
