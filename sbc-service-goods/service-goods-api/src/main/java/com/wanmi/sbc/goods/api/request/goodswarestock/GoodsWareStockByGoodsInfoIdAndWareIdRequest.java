package com.wanmi.sbc.goods.api.request.goodswarestock;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @ClassName GoodsWareStockByGoodsInfoIdsRequest
 * @Description TODO
 * @Author lvzhenwei
 * @Date 2020/4/17 18:47
 **/
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsWareStockByGoodsInfoIdAndWareIdRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "skuId")
    @NotNull
    private String goodsInfoId;

    @ApiModelProperty(value = "仓库id")
    @NotNull
    private Long wareId;
}
