package com.wanmi.sbc.goods.api.request.info;

import com.wanmi.sbc.goods.bean.dto.GoodsInfoLockStockDTO;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoMinusStockDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;
import java.util.List;

/**
 * 商品SKU批量库存锁定请求
 * Created by chenchang on 2023-5-30.
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsInfoLockStockRequest implements Serializable {


    private static final long serialVersionUID = -1320374247772476661L;

    /**
     * 批量商品库存数据
     */
    @ApiModelProperty(value = "批量商品库存数据")
    @NotEmpty
    private List<GoodsInfoLockStockDTO> stockList;

    /**
     * 仓库Id
     */
    private Long wareId;


    /**
     * 业务ID
     */
    private String businessId;

}
