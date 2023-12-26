package com.wanmi.sbc.goods.api.request.info;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsInfoCatWareRequest implements Serializable {

    /**
     * 活动id
     */
    private Long marketingId;

    /**
     * 仓库id
     */
    private Long wareId;
}
