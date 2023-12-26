package com.wanmi.sbc.goods.goodsattribute.reponse;


import com.wanmi.sbc.goods.api.response.goods.GoodsInsidePageResponse;

import com.wanmi.sbc.goods.goodsattribute.root.GoodsAttribute;

import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;
/**
 * 商品属性
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:25
 * @Description: TODO
 * @Version 1.0
 */
@Data
public class GoodsAttrQueryResponse {

    /**
     * 商品属性分页数据
     */
    private Page<GoodsAttribute> goodsPage = new PageImpl<>(new ArrayList<>());


    /**
     * 商品属性数据
     */
    private GoodsInsidePageResponse goodsPages;

}
