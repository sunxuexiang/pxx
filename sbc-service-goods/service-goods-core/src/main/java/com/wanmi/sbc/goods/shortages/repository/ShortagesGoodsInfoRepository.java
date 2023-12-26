package com.wanmi.sbc.goods.shortages.repository;

import com.wanmi.sbc.goods.shortages.model.root.ShortagesGoodsInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShortagesGoodsInfoRepository extends JpaRepository<ShortagesGoodsInfo, String>, JpaSpecificationExecutor<ShortagesGoodsInfo> {

    @Query(value = "from ShortagesGoodsInfo where checkTime = ?1")
    List<ShortagesGoodsInfo> queryShortagesGoodsInfosByCheckTime(LocalDateTime checkTime);

    @Modifying
    @Query(value = "delete from ShortagesGoodsInfo where checkTime = ?1")
    void deleteShortagesGoodsInfosByCheckTime(LocalDateTime checkTime);
}
