package com.wanmi.sbc.goods.api.response.goods;

import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author shiGuangYi
 * @createDate 2023-06-29 18:10
 * @Description: TODO
 * @Version 1.0
 */
@ApiModel
@Data
public class GoodsSkuInfoResponse implements Serializable {

    /**
     * 商品SKU全部数据
     */
    @ApiModelProperty(value = "商品SKU全部数据")
    private List<GoodsInfoVO> goodsInfoList;
}
