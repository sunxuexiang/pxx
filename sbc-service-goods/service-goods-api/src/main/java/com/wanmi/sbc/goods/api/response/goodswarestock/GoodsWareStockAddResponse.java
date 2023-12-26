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
 * <p>sku分仓库存表新增结果</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:22:56
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsWareStockAddResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 已新增的sku分仓库存表信息
     */
    @ApiModelProperty(value = "已新增的sku分仓库存表信息")
    private GoodsWareStockVO goodsWareStockVO;
}
