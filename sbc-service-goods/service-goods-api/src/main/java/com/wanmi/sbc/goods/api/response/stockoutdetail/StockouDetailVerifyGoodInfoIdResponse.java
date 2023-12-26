package com.wanmi.sbc.goods.api.response.stockoutdetail;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @program: sbc_h_tian
 * @description: 单独sku校验
 * @author: Mr.Tian
 * @create: 2020-06-04 11:26
 **/
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockouDetailVerifyGoodInfoIdResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 根据sku返回用户是否加入到了sku缺货明细
     *   true 未插入过任何一条记录 false 已插入记录
     */
    @ApiModelProperty(value = "根据sku返回用户是否加入到了sku缺货明细，true 未插入过任何一条记录 false 已插入记录")
    private Boolean flag;
}
