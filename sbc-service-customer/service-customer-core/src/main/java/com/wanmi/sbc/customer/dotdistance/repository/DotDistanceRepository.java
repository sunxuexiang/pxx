package com.wanmi.sbc.customer.dotdistance.repository;

import com.wanmi.sbc.customer.dotdistance.model.root.DotDistance;
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
public interface DotDistanceRepository extends JpaRepository<DotDistance, Long>,
        JpaSpecificationExecutor<DotDistance> {


    /**
     * 通过用户地址id查询网点距离表
     * @param  deliveryAddressId
     * @return
     */
    @Query(value = "select  * from dot_distance where delivery_address_id = ?1  ",nativeQuery = true)
    List<DotDistance> getDotDistancesByDotDistanceIdIs(String deliveryAddressId);

    /**
     *获取最小距离
     */
    @Query(value = "select min(distance),network_id from dot_distance where delivery_address_id = ?1  group by delivery_address_id",nativeQuery = true)
    Object getMinDistanceByDeliveryAddressId();

    /**
     * 通过用户地址id删除数据
     * @param deliveryAddressId
     * @return
     */
    @Modifying
    @Query(value = "delete from dot_distance where delivery_address_id = ?1",nativeQuery = true)
    int deleteByDeliveryAddressId(String deliveryAddressId);

    /**
     * 通过网点id删除数据
     * @param deliveryAddressId
     * @return
     */
    @Modifying
    @Query(value = "delete from dot_distance where network_id in (?1)",nativeQuery = true)
    int deleteByNetWorkIds(List<Long> deliveryAddressId);
}
