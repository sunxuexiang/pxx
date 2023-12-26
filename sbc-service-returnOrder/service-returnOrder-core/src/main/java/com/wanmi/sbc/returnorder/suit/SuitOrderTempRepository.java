package com.wanmi.sbc.returnorder.suit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SuitOrderTempRepository extends JpaRepository<SuitOrderTemp,Long>, JpaSpecificationExecutor<SuitOrderTemp> {

    @Query(value = "select sum(suit_buy_num) from suit_order_temp WHERE customer_id = ?1 AND marketing_id = ?2 group by customer_id", nativeQuery = true)
    Integer getSuitOrderTempsByCustomerIdAndMarketingId(String customerId, Long marketingId);

    SuitOrderTemp queryFirstByOrderCode(String orderCode);
}
