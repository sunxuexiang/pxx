package com.wanmi.sbc.goods.api.request.stockoutdetail;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * @program: sbc_h_tian
 * @description: SPU维度进行校验
 * @author: Mr.Tian
 * @create: 2020-06-04 09:24
 **/
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockoutDetailVerifyRequest  extends BaseQueryRequest {
    /**
     * 商品spu
     */
    @ApiModelProperty(value = "商品spu")
    private String goodsId;

    /**
     * 会员id
     */
    @ApiModelProperty(value = "会员id")
    private String customerId;

}
