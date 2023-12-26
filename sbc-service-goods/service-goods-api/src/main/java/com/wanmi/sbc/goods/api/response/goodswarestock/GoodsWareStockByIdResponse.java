package com.wanmi.sbc.goods.api.response.goodswarestock;

import com.wanmi.sbc.goods.bean.vo.GoodsWareStockVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>根据id查询任意（包含已删除）sku分仓库存表信息response</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:22:56
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsWareStockByIdResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * sku分仓库存表信息
     */
    @ApiModelProperty(value = "sku分仓库存表信息")
    private GoodsWareStockVO goodsWareStockVO;
}
