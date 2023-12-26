package com.wanmi.sbc.goods.api.request.stockoutdetail;

import com.wanmi.sbc.goods.api.request.GoodsBaseRequest;
import lombok.*;
import javax.validation.constraints.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>单个删除缺货管理请求参数</p>
 * @author tzx
 * @date 2020-05-27 10:48:14
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockoutDetailDelByIdRequest extends GoodsBaseRequest {
private static final long serialVersionUID = 1L;

    /**
     * 缺货明细
     */
    @ApiModelProperty(value = "缺货明细")
    @NotNull
    private String stockoutDetailId;
}
