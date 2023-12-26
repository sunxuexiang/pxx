package com.wanmi.sbc.goods.api.request.goods;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 根据Erp编号批量查询商品信息请求对象
 * @author yang
 * @dateTime 2021/01/04 上午9:40
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoodsListByErpGoodsInfoNosRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * erpGoodsInfoNos
     */
    @ApiModelProperty(value = "erpGoodsInfoNos")
    private List<String> erpGoodsInfoNos;
}
