package com.wanmi.sbc.goods.api.request.goodswarestock;


import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.goods.api.request.GoodsBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Objects;


/**
 * 根据区域ids查询商品库存request
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsWareStockByAreaIdsRequest extends GoodsBaseRequest {

    private static final long serialVersionUID = 7121650379691905031L;

    /**
     * 单品ids
     */
    @NotEmpty
    @ApiModelProperty(value = "单品id")
    private List<String> goodsInfoIds;

    /**
     * 省份id
     */
    @ApiModelProperty(value = "身份id")
    private Long provinceId;

    /**
     * 城市id
     */
    @ApiModelProperty(value = "城市id")
    private Long cityId;

    @Override
    public void checkParam() {
        // 要么省市都传、要么都不传
        if (Objects.nonNull(provinceId) && Objects.nonNull(cityId)) return;
        if (Objects.isNull(provinceId) && Objects.isNull(cityId)) return;
        throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
    }
}
