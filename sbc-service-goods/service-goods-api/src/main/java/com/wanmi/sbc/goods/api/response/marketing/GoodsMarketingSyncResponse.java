package com.wanmi.sbc.goods.api.response.marketing;

import com.wanmi.sbc.goods.bean.vo.GoodsMarketingVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * <p>商品营销</p>
 * author: yang
 * Date: 2020-12-26
 */
@ApiModel
@Data
public class GoodsMarketingSyncResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商品营销关系列表")
    private List<GoodsMarketingVO> goodsMarketingList;
}
