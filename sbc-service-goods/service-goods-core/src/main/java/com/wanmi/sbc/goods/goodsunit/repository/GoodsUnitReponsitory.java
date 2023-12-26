package com.wanmi.sbc.goods.goodsunit.repository;

import com.wanmi.sbc.goods.goodsunit.root.StoreGoodsUnit;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsUnitReponsitory extends JpaRepository<StoreGoodsUnit, Long>, JpaSpecificationExecutor<StoreGoodsUnit> {
    @Query("update StoreGoodsUnit p set p.delFlag = 1 where p.storeGoodsUnitId = :storeGoodsUnitId")
    @Modifying
    int modifyDelFlagById(@Param("storeGoodsUnitId") String storeGoodsUnitId);



    @Query(value = "select * from store_goods_unit WHERE store_goods_unit_id = ?1",nativeQuery = true)
    StoreGoodsUnit getByStoreGoodsUnitId( @Param("storeGoodsUnitId") String storeGoodsUnitId);
}
