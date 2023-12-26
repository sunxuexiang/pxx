package com.wanmi.sbc.goods.api.response.goodswarestockdetail;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.goods.bean.vo.GoodsWareStockDetailVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p> 库存明细表分页结果</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:24:37
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsWareStockDetailPageResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     *  库存明细表分页结果
     */
    @ApiModelProperty(value = " 库存明细表分页结果")
    private MicroServicePage<GoodsWareStockDetailVO> goodsWareStockDetailVOPage;
}
