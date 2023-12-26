package com.wanmi.sbc.goods.api.response.goodswarestock;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.goods.bean.vo.GoodsWareStockPageVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>sku分仓库存表分页结果</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:22:56
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsWareStockPageResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * sku分仓库存表分页结果
     */
    @ApiModelProperty(value = "sku分仓库存表分页结果")
    private MicroServicePage<GoodsWareStockPageVO> goodsWareStockVOPage;
}
