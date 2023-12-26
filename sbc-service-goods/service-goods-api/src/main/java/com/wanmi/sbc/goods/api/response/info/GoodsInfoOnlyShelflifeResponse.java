package com.wanmi.sbc.goods.api.response.info;

import com.wanmi.sbc.goods.bean.dto.GoodsInfoOnlyShelflifeDTO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author jeffrey
 * @create 2021-08-04 17:09
 */

@Data
public class GoodsInfoOnlyShelflifeResponse {


    /**
     * 商品SKU信息
     */
    @ApiModelProperty(value = "商品SKU信息,仅包含保质期信息")
    private List<GoodsInfoOnlyShelflifeDTO> goodsInfos;
}
