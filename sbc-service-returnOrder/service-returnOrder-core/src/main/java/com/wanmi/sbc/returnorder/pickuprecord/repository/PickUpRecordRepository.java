package com.wanmi.sbc.returnorder.pickuprecord.repository;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.returnorder.pickuprecord.model.root.PickUpRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * <p>测试代码生成DAO</p>
 * @author lh
 * @date 2020-07-14 13:48:26
 */
@Repository
public interface PickUpRecordRepository extends JpaRepository<PickUpRecord, String>,
        JpaSpecificationExecutor<PickUpRecord> {

    /**
     * 单个删除测试代码生成
     * @author lh
     */
    @Modifying
    @Query("update PickUpRecord set delFlag = 1 where pickUpId = ?1")
    void deleteById(String pickUpId);

    /**
     * 批量删除测试代码生成
     * @author lh
     */
    @Modifying
    @Query("update PickUpRecord set delFlag = 1 where pickUpId in ?1")
    void deleteByIdList(List<String> pickUpIdList);

    Optional<PickUpRecord> findByPickUpIdAndDelFlag(String id, DeleteFlag delFlag);

    Optional<PickUpRecord> findByTradeIdAndDelFlag(String tradeId,DeleteFlag deleteFlag);

    /**
     * 修改自提标志位
     * @author lh
     */
    @Modifying
    @Query("update PickUpRecord set pickUpFlag=1,pickUpTime=NOW()  where tradeId = ?1")
    void updatePickUpFlag(String tradeId);

}
