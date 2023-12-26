package com.wanmi.sbc.customer.points.repository;

import com.wanmi.sbc.customer.bean.enums.OperateType;
import com.wanmi.sbc.customer.points.model.root.CustomerPointsDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>会员积分明细DAO</p>
 */
@Repository
public interface CustomerPointsDetailRepository extends JpaRepository<CustomerPointsDetail, Long>,
        JpaSpecificationExecutor<CustomerPointsDetail> {

    /**
     * 查询积分统计
     */
    @Query("select sum(c.pointsAvailable) as pointsAvailable,sum(c.pointsUsed) as pointsUsed from Customer c")
    List getPointsStatistics();

    /**
     * 根据用户id列表查询积分统计
     */
    @Query("select sum(c.pointsAvailable) as pointsAvailable,sum(c.pointsUsed) as pointsUsed from Customer c where c.customerId in :customerIds")
    List getPointsStatisticsByCustomerIds(@Param("customerIds") List<String> customerIds);

    /**
     * 根据会员id、积分增减类型、查询即将过期积分
     */
    @Query("SELECT sum( c.points ) FROM CustomerPointsDetail c WHERE c.customerId = :customerId AND c.type = :type and c.opTime < :opTime")
    Long queryWillExpirePoints(@Param("customerId") String customerId, @Param("type") OperateType type, @Param("opTime") LocalDateTime opTime);
}
