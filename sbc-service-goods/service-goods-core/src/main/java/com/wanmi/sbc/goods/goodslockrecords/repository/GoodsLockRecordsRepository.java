package com.wanmi.sbc.goods.goodslockrecords.repository;

import com.wanmi.sbc.goods.goodslockrecords.model.root.GoodsLockRecords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author chenchang
 * @since 2023/05/31 11:43
 */
@Repository
public interface GoodsLockRecordsRepository extends JpaRepository<GoodsLockRecords, String>,
        JpaSpecificationExecutor<GoodsLockRecords> {

    @Modifying
    @Query("update GoodsLockRecords d set d.updateTime = now(),d.lockCount = d.lockCount+1  where d.businessId =:#{#info.businessId} and d.delFlag =0")
    int reLock(@Param("info") GoodsLockRecords goodsLockRecords);

    @Modifying
    @Query(value = "insert into goods_lock_records ( business_id, lock_content, create_time, update_time, lock_count,del_flag) " +
            " VALUES (" +
            " :#{#info.businessId}," +
            " :#{#info.lockContent}," +
            " :#{#info.createTime}," +
            " :#{#info.updateTime}," +
            "1," +
            "0" +
            ") ", nativeQuery = true)
    int lock(@Param("info") GoodsLockRecords goodsLockRecords);

    @Modifying
    @Query("update GoodsLockRecords d set d.updateTime = now(),d.lockCount = d.lockCount+1,d.delFlag=0 where d.businessId =:#{#info.businessId} and d.delFlag =1")
    int reliveLock(@Param("info") GoodsLockRecords goodsLockRecords);

    @Modifying
    @Query("update GoodsLockRecords d set d.delFlag =1  where d.businessId =?1 and d.delFlag =0")
    int invalidLock(String businessId);
}
