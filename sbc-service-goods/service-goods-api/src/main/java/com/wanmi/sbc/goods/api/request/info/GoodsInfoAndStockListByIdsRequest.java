package com.wanmi.sbc.goods.api.request.info;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;
import java.util.List;

/**
 * 根据商品SKU编号查询请求
 * Created by daiyitian on 2017/3/24.
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsInfoAndStockListByIdsRequest implements Serializable {

    private static final long serialVersionUID = -2265501195719873212L;

    /**
     * 批量SKU编号
     */
    @ApiModelProperty(value = "批量SKU编号")
    @NotEmpty
    private List<String> goodsInfoIds;

    /**
     * 批量拆箱编号
     */
    @ApiModelProperty(value = "批量拆箱编号")
    private List<Long> devanningIds;

    @ApiModelProperty(value = "是否匹配到仓")
    private Boolean matchWareHouseFlag;

    @ApiModelProperty(value = "仓库id")
    private Long wareId;
}
