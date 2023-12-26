package com.wanmi.sbc.goods.info.repository;

import com.wanmi.sbc.goods.info.model.root.GoodsTag;
import com.wanmi.sbc.goods.info.model.root.GoodsTagRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsTagRepository extends JpaRepository<GoodsTag, Long>, JpaSpecificationExecutor<GoodsTag> {
}
