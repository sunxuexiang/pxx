package com.wanmi.sbc.goods.api.request.goodswarestock;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName GoodsWareStockAddListRequest
 * @Description 批量新增sku分仓库存数据参数
 * @Author lvzhenwei
 * @Date 2020/4/9 15:45
 **/
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsWareStockAddListRequest extends BaseRequest {

    /**
     * sku分仓库存数据参数List
     */
    @ApiModelProperty(value = "sku分仓库存数据参数List")
    @NotNull
    List<GoodsWareStockAddRequest> goodsWareStockAddRequestList;
}
