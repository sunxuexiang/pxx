package com.wanmi.sbc.goods.api.response.brand;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.goods.bean.vo.GoodsBrandRecommendVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@ApiModel
//@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsBrandRecommendResponse implements Serializable {

    /**
     * 品牌推荐列表
     */
    @ApiModelProperty(value = "品牌推荐列表")
    private MicroServicePage<GoodsBrandRecommendVO> goodsBrandRecommendVOList;
}
