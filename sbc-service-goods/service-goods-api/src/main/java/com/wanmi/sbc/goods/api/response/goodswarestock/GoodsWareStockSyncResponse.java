package com.wanmi.sbc.goods.api.response.goodswarestock;

import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author caofang
 * @date 2020/6/2 10:39
 * @Description
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsWareStockSyncResponse implements Serializable {

    private static final long serialVersionUID = -8405665251031123883L;

/**
    sku集合
     */
    @ApiModelProperty(value = "sku集合")
    private List<GoodsInfoVO> goodsInfoVOS;
}
