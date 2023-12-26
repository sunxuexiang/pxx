package com.wanmi.sbc.goods.api.request.retailgoodscommend;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 批量新增推荐商品请求参数类
 * @author: XinJiang
 * @time: 2022/4/20 9:48
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetailGoodsRecommendSettingBatchAddRequest implements Serializable {

    private static final long serialVersionUID = -2265074076449790984L;

    /**
     * 商品skuIds
     */
    @ApiModelProperty(value = "商品skuIds")
    private List<String> goodsInfoIds;
}
