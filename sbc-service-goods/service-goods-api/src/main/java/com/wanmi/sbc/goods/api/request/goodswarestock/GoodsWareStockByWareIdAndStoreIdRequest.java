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
public class GoodsWareStockByWareIdAndStoreIdRequest implements Serializable {


    private static final long serialVersionUID = -1944292342871691372L;
    /**
     * goodsInfoIdList或者goodsIdList
     */
    @ApiModelProperty(value = "主键")
    @NotNull
    private List<String> goodsForIdList;

    @ApiModelProperty(value = "仓库Id")
    private Long wareId;

    @ApiModelProperty(value = "店铺Id")
    private Long storeId;
}
