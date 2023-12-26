package com.wanmi.sbc.goods.api.request.info;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FindBrandsHasAddedSkuRequest {
    /**
     * 品牌id集合
     */
    List<Long> brandIds;
    /**
     * 仓库id
     */
    Long wareId;
}
