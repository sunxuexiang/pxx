package com.wanmi.sbc.goods.goodsdevanning.reponse;

import com.wanmi.sbc.goods.bean.vo.GoodsDevanningVO;
import com.wanmi.sbc.goods.info.model.root.GoodsPropDetailRel;
import lombok.Data;

import java.util.List;


@Data
public class GoodsDevanningResponse {


    /**
     * 商品属性列表
     */
    private List<GoodsDevanningVO> goodsDevanningVOS;
}
