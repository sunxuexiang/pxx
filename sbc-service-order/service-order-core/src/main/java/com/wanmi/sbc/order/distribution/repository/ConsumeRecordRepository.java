package com.wanmi.sbc.order.distribution.repository;

import com.wanmi.sbc.order.distribution.model.root.ConsumeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description: 消费记录Repository
 * @Autho qiaokang
 * @Date：2019-03-05 16:39:04
 */
@Repository
public interface ConsumeRecordRepository extends JpaRepository<ConsumeRecord, String>,
        JpaSpecificationExecutor<ConsumeRecord> {

    /**
     * 更新消费记录
     *
     * @param consumeRecord
     * @return
     */
    @Modifying
    @Query(value = "update consume_record r set r.valid_consume_sum = :#{#consumeRecord.validConsumeSum} " +
            "where r.order_id = :#{#consumeRecord.orderId}", nativeQuery = true)
    int modifyConsumeRecord(@Param("consumeRecord") ConsumeRecord consumeRecord);

    /**
     * 统计累计的有效消费金额，订单数，
     *
     * @return
     */
    @Query(value = "SELECT r.customer_id ,COUNT(*) ,SUM(r.valid_consume_sum) ,r.customer_name  " +
            "FROM consume_record r WHERE r.customer_id IN(:ids) and r.valid_consume_sum > 0 GROUP BY r.customer_id", nativeQuery = true)
    List<Object> countValidConsume(@Param("ids") List<String> ids);

    /**
     * 统计累计的消费金额，订单数，
     *
     * @return
     */
    @Query(value = "SELECT r.customer_id ,COUNT(*) ,SUM(r.consume_sum)  ,r.customer_name  " +
            "FROM consume_record r WHERE r.customer_id IN(:ids) GROUP BY r.customer_id", nativeQuery = true)
    List<Object> countConsume(@Param("ids") List<String> ids);

    @Query(value = "SELECT r.customer_id ,COUNT(*) ,SUM(r.consume_sum), r.customer_name ,r.head_img, r" +
            ".order_create_time FROM consume_record r WHERE r.distribution_customer_id=?1 and r" +
            ".distribution_customer_id != r.customer_id " +
            "GROUP BY r.customer_id ORDER BY r.order_create_time DESC limit ?2,?3", nativeQuery = true)
    List<Object> pageByCustomerId(String customerId,int startNum,int endNum);

    @Query(value = "SELECT COUNT(DISTINCT r.customer_id) FROM consume_record r WHERE r.distribution_customer_id=?1 " +
            "and r.distribution_customer_id != r.customer_id", nativeQuery = true)
    Integer pageByDistributionCustomerIdCountNum(String customerId);

}
