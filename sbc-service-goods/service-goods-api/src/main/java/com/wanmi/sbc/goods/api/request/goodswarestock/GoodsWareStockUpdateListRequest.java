package com.wanmi.sbc.goods.api.request.goodswarestock;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName: GoodsWareStockUpdateListRequest
 * @Description: TODO
 * @Date: 2020/6/1 16:51
 * @Version: 1.0
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsWareStockUpdateListRequest extends BaseRequest {

    /**
     * sku分仓库存数据参数List
     */
    @ApiModelProperty(value = "需要更新的库存")
    @NotNull
    List<GoodsWareStockUpdateRequest> goodsWareStockAddRequestList;

    /**
     * sku分仓库存数据参数List
     */
    @ApiModelProperty(value = "更新前的库存")
    @NotNull
    List<GoodsWareStockUpdateRequest> goodsWareStockOldRequestList;
}
