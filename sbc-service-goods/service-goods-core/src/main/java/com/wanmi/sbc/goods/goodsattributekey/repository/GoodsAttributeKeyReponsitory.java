package com.wanmi.sbc.goods.goodsattributekey.repository;

import com.wanmi.sbc.goods.goodsattributekey.root.GoodsAttributeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoodsAttributeKeyReponsitory extends JpaRepository<GoodsAttributeKey, Long>, JpaSpecificationExecutor<GoodsAttributeKey> {
    @Query(value = "SELECT * from goods_attribute_key WHERE goods_id in (?1)",nativeQuery = true)
    List<GoodsAttributeKey> findByGoodsIdIn(List<String> goodsId);
    @Query(value = "SELECT * from goods_attribute_key WHERE goods_info_id in (?1)",nativeQuery = true)
    List<GoodsAttributeKey> findByGoodsInfoIdIn(List<String> goodsInfoIds);
}
