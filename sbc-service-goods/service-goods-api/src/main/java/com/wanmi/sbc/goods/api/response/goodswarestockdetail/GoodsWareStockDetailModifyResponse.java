package com.wanmi.sbc.goods.api.response.goodswarestockdetail;

import com.wanmi.sbc.goods.bean.vo.GoodsWareStockDetailVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p> 库存明细表修改结果</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:24:37
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsWareStockDetailModifyResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 已修改的 库存明细表信息
     */
    @ApiModelProperty(value = "已修改的 库存明细表信息")
    private GoodsWareStockDetailVO goodsWareStockDetailVO;
}
