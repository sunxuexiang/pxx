package com.wanmi.sbc.goods.api.response.goodswarestock;

import com.wanmi.sbc.goods.bean.vo.GoodsWareStockVO;
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
 * @author zhangwenchang
 * @date 2020-04-06 17:22:56
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsWareStockListResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * sku分仓库存表列表结果
     */
    @ApiModelProperty(value = "sku分仓库存表列表结果")
    private List<GoodsWareStockVO> goodsWareStockVOList;
}
