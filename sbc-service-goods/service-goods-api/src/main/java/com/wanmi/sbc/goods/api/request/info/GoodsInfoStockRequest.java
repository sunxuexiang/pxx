package com.wanmi.sbc.goods.api.request.info;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品库存查询
 * Created by yang on 2021/1/22.
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsInfoStockRequest implements Serializable {

    private static final long serialVersionUID = 4997682665530970684L;


    private String skuId;

    private Long byCount;

    /**
     * 除数标识
     */
    private BigDecimal divisorFlag =BigDecimal.ONE;

    private Long devanningId;
}
