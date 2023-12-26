package com.wanmi.sbc.goods.api.response.goodsrecommendsetting;

import com.wanmi.sbc.goods.bean.vo.GoodsRecommendSettingSimpleVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>根据id查询任意（包含已删除）商品推荐配置信息response</p>
 * @author chenyufei
 * @date 2019-09-07 10:24:37
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsRecommendSettingSimpleResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 商品推荐配置信息
     */
    @ApiModelProperty(value = "商品推荐配置信息")
    private GoodsRecommendSettingSimpleVO goodsRecommendSettingVO;
}
