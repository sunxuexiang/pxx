package com.wanmi.sbc.goods.api.request.goods;

import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.goods.api.request.icitem.IcitemUpdateRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * 特价商品新增请求实体
 * Created by baijz on 2017/3/24.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsWeightRequest extends BaseRequest {

    /**
     * 批次号
     */
    @ApiModelProperty(value = "批次号")
    private GoodsByConditionRequest goodsByConditionRequest;

    @ApiModelProperty(value = "批次号")
    private Map<String, IcitemUpdateRequest> map;

    @ApiModelProperty(value = "standardIds")
    private List<String> goodsStandardIds;

    @ApiModelProperty(value = "standard表Map")
    private Map<String, IcitemUpdateRequest> standardMap;
}
