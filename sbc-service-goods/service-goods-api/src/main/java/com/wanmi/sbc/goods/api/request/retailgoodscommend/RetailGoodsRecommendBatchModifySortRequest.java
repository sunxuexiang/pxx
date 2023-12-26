package com.wanmi.sbc.goods.api.request.retailgoodscommend;

import com.wanmi.sbc.goods.bean.dto.RetailGoodsRecommendSortDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @description: 修改散批鲸喜推荐商品排序请求参数类
 * @author: XinJiang
 * @time: 2022/4/20 10:44
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetailGoodsRecommendBatchModifySortRequest implements Serializable {

    private static final long serialVersionUID = 297212471178304990L;

    /**
     * 批量修改散批鲸喜推荐商品排序
     */
    @NotEmpty
    @ApiModelProperty(value = "批量修改散批鲸喜推荐商品排序", required = true)
    private List<RetailGoodsRecommendSortDTO> retailGoodsRecommendSortDTOS;
}
