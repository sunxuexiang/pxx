package com.wanmi.sbc.goods.goodsBackups.repository;

import com.wanmi.sbc.goods.goodsBackups.root.GoodsRecommendBackups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsRecommendBackupsReponsitory extends JpaRepository<GoodsRecommendBackups, Long>, JpaSpecificationExecutor<GoodsRecommendBackups> {

}
