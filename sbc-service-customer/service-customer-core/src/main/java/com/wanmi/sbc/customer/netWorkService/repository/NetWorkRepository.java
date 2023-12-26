package com.wanmi.sbc.customer.netWorkService.repository;

import com.wanmi.sbc.customer.netWorkService.model.root.NetWork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

/**
 * 网点查询
 */
@Repository
public interface NetWorkRepository extends JpaRepository<NetWork, Long>,
        JpaSpecificationExecutor<NetWork> {

    /**
     *
     * @param lat 纬度
     * @param lng 经度
     */
    @Modifying
    @Transactional
    @Query(value = "update network set lat =?1 , lng = ?2 where network_id = ?3",nativeQuery = true)
    void updateRecommendByRoomId(BigDecimal lat, BigDecimal lng,Long networkId);


    /**
     * 批量删除网点
     * @param list
     */
    @Modifying
    @Transactional
    @Query(value = "update network set del_flag =1 , delete_time = now()   where network_id in ?1",nativeQuery = true)
    void deleteNetWorkByNetworkIds(List<Long> list);


    /**
     * 批量启动网点
     * @param list
     */
    @Modifying
    @Transactional
    @Query(value = "update network set del_flag =0 , delete_time = null   where network_id in ?1",nativeQuery = true)
    void startNetWorkByNetworkIds(List<Long> list);


    @Query(value = "SELECT * from network WHERE lat is null  and province = ?1 LIMIT 300 ",nativeQuery = true)
    List<NetWork> getNeedQuryData(String province);


    @Query(value = "SELECT * from network WHERE lat is not null  and province = ?1 ",nativeQuery = true)
    List<NetWork> getAllNeedQuryData(String province);
}
