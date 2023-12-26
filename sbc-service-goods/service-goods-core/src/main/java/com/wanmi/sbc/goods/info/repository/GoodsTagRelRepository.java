package com.wanmi.sbc.goods.info.repository;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.goods.info.model.root.GoodsTagRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Repository
public interface GoodsTagRelRepository extends JpaRepository<GoodsTagRel, Long>, JpaSpecificationExecutor<GoodsTagRel> {

    @Transactional
    @Modifying
    @Query("update GoodsTagRel g set g.delFlag = ?1 where g.tagId = ?2 and g.goodsId in ?3")
    int updateDelFlagByTagIdAndGoodsIdIn(DeleteFlag delFlag, Long tagId, Collection<String> goodsIds);
}
