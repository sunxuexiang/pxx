package com.wanmi.sbc.setting.doorpick.repository;


import com.wanmi.sbc.setting.doorpick.model.root.DoorPickConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * 网点查询
 */
@Repository
public interface DoorPickConfigRepository extends JpaRepository<DoorPickConfig, Long>,
        JpaSpecificationExecutor<DoorPickConfig> {
    /**
     * 批量删除网点
     * @param list
     */
    @Modifying
    @Transactional
    @Query(value = "update door_pick_config set del_flag =1 , delete_time = now()   where network_id in ?1",nativeQuery = true)
    void deleteDoorPickConfigByNetworkIds(List<Long> list);

    /**
     * 批量停用网点
     * @param list
     */
    @Modifying
    @Transactional
    @Query(value = "update door_pick_config set del_flag =2 , delete_time = now()   where network_id in ?1",nativeQuery = true)
    void stopDoorPickConfigByNetworkIds(List<Long> list);

    /**
     * 批量启动网点
     * @param list
     */
    @Modifying
    @Transactional
    @Query(value = "update door_pick_config set del_flag =0 , delete_time = null   where network_id in ?1",nativeQuery = true)
    void startDoorPickConfigByNetworkIds(List<Long> list);


    @Query(value = "SELECT * from door_pick_config WHERE province = ?1 ",nativeQuery = true)
    List<DoorPickConfig> getAllNeedQuryData(String province);

    @Query(value = "SELECT networkId from DoorPickConfig WHERE storeId = ?1 And delFlag=0")
    List<Long> getOpenNetWorkIdByStoreId(Long storeId);
}
