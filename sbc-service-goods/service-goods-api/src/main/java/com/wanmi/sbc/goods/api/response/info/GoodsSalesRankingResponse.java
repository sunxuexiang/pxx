package com.wanmi.sbc.goods.api.response.info;

import com.wanmi.sbc.goods.bean.vo.GoodsBySalesRankingVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 商品销量排行
 * @author jeffrey
 * @create 2021-08-07 9:49
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsSalesRankingResponse {
    private static final long serialVersionUID = -4174954292041196452L;

    @ApiModelProperty(value = "商品SKU信息")
    private List<GoodsBySalesRankingVO> rankingVOList;
}
