package com.wanmi.sbc.goods.info.reponse;

import com.wanmi.sbc.goods.info.model.root.Goods;
import lombok.Data;

/**
 * 商品视图响应
 * Created by daiyitian on 2017/3/24.
 */
@Data
public class GoodsResponse {

    /**
     * 商品信息
     */
    private Goods goods;
}
