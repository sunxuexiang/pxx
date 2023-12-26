package com.wanmi.sbc.goods.bean.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

/**
 * 新增特价商品
 * Created by dyt on 2017/4/11.
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpecialGoodsAddDTO implements Serializable {

    private static final long serialVersionUID = -8807674093765870168L;

    /**
     * 批次号
     */
    @ApiModelProperty(value = "批次号")
    private String goodsInfoBatchNo;

    /**
     * 库存
     */
    @ApiModelProperty(value = "库存")
    private Long stock;

}
