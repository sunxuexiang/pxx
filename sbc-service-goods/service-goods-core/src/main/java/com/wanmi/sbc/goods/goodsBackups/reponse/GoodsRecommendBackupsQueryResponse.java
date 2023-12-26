package com.wanmi.sbc.goods.goodsBackups.reponse;


import com.wanmi.sbc.goods.api.response.goods.GoodsInsidePageResponse;
import com.wanmi.sbc.goods.goodsBackups.root.GoodsRecommendBackups;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;

/**
 * 商品推荐备份
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:25
 * @Description: TODO
 * @Version 1.0
 */
@Data
public class GoodsRecommendBackupsQueryResponse {

    /**
     * 商品属性分页数据
     */
    private Page<GoodsRecommendBackups> goodsPage = new PageImpl<>(new ArrayList<>());


    /**
     * 商品属性数据
     */
    private GoodsInsidePageResponse goodsPages;

}
