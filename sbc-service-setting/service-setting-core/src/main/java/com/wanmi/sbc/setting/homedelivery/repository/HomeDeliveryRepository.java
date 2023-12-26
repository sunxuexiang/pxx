package com.wanmi.sbc.setting.homedelivery.repository;

import com.wanmi.sbc.setting.homedelivery.model.root.HomeDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.wanmi.sbc.common.enums.DeleteFlag;

import java.util.Optional;
import java.util.List;

/**
 * <p>配送到家DAO</p>
 * @author lh
 * @date 2020-08-01 14:13:32
 */
@Repository
public interface HomeDeliveryRepository extends JpaRepository<HomeDelivery, Long>,
        JpaSpecificationExecutor<HomeDelivery> {

    /**
     * 单个删除配送到家
     * @author lh
     */
    @Modifying
    @Query("update HomeDelivery set delFlag = 1 where homeDeliveryId = ?1")
    void deleteById(Long homeDeliveryId);

    /**
     * 批量删除配送到家
     * @author lh
     */
    @Modifying
    @Query("update HomeDelivery set delFlag = 1 where homeDeliveryId in ?1")
    void deleteByIdList(List<Long> homeDeliveryIdList);

    Optional<HomeDelivery> findByHomeDeliveryIdAndDelFlag(Long id, DeleteFlag delFlag);

    @Query("from HomeDelivery where delFlag = 0 and storeId=?1")
    List<HomeDelivery> queryByStoreId(Long storeId);

    @Query("from HomeDelivery where delFlag = 0 and storeId=?1 and deliveryType=?2")
    List<HomeDelivery> queryByStoreIdAndDeliveryType(Long storeId, Integer deliveryType);

    @Modifying
    @Query("update HomeDelivery set content = ?2 where homeDeliveryId = ?1")
    void updateById(Long homeDeliveryId,String content);

    @Modifying
    @Query("update HomeDelivery set content = ?3 where storeId = ?1 and delFlag = 0 and deliveryType=?2")
    void updateByStoreIdIdAndDeliveryType(Long storeId, Integer deliveryType, String content);

}
