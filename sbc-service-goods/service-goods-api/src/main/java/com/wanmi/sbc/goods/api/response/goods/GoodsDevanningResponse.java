package com.wanmi.sbc.goods.api.response.goods;

import com.wanmi.sbc.goods.bean.vo.GoodsDevanningVO;
import lombok.Data;

import java.util.List;


@Data
public class GoodsDevanningResponse {


    /**
     * 商品属性列表
     */
    private List<GoodsDevanningVO> goodsDevanningVOS;
}
