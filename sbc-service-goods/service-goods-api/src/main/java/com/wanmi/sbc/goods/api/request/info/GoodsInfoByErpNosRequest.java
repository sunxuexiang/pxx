package com.wanmi.sbc.goods.api.request.info;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 根据商品SKU编号查询请求
 * Created by daiyitian on 2017/3/24.
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsInfoByErpNosRequest implements Serializable {


    private static final long serialVersionUID = -5762220492981045582L;
    /**
     * erpGoodsInfoNos
     */
    @ApiModelProperty(value = "erpGoodsInfoNos")
    private List<String> erpGoodsInfoNos;

    @ApiModelProperty(value = "仓库id")
    private Long wareId;

    @ApiModelProperty(value = "是否匹配到仓")
    private Boolean matchWareHouseFlag;

}
