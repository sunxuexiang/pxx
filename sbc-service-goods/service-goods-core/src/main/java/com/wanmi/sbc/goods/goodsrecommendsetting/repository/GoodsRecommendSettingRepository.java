package com.wanmi.sbc.goods.goodsrecommendsetting.repository;

import com.wanmi.sbc.goods.goodsrecommendsetting.model.root.GoodsRecommendSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


/**
 * <p>商品推荐配置DAO</p>
 * @author chenyufei
 * @date 2019-09-07 10:24:37
 */
@Repository
public interface GoodsRecommendSettingRepository extends JpaRepository<GoodsRecommendSetting, String>,
        JpaSpecificationExecutor<GoodsRecommendSetting> {

}
