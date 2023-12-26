package com.wanmi.sbc.returnorder.api.response.follow;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoNewVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class FollowListNewResponse {

    /**
     * 商品SKU信息
     */
    @ApiModelProperty(value = "商品SKU信息")
    private MicroServicePage<GoodsInfoNewVO> goodsInfos;
}
