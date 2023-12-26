package com.wanmi.sbc.goods.api.request.info;

import com.wanmi.sbc.goods.bean.dto.GoodsInfoUnlockStockDTO;
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
 * 商品SKU库存增量请求
 * Created by chenchang on 2023/5/31.
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsInfoUnlockStockRequest implements Serializable {


    private static final long serialVersionUID = 5977088808615314350L;

    /**
     * 批量商品库存数据
     */
    @ApiModelProperty(value = "批量商品库存数据")
    @NotEmpty
    private List<GoodsInfoUnlockStockDTO> stockList;

    /**
     * 仓库Id
     */
    private Long wareId;

    /**
     * 业务ID
     */
    private String businessId;

}
