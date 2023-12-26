package com.wanmi.sbc.goods.info.reponse;

import com.wanmi.sbc.goods.info.model.root.GoodsPropDetailRel;
import lombok.Data;

import java.util.List;

/**
 * @discription 商品图文信息+属性response
 * @author yangzhen
 * @date 2020/9/3 11:14
 * @param
 * @return
 */
@Data
public class GoodsDetailResponse {

    /**
     * 商品图文信息
     */
    private String goodsDetail;

    /**
     * 商品属性列表
     */
    private List<GoodsPropDetailRel> goodsPropDetailRels;
}
