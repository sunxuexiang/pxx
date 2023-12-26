package com.wanmi.sbc.order.village;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderVillageAddDeliveryRepository extends JpaRepository<OrderVillageAddDelivery, Long>, JpaSpecificationExecutor<OrderVillageAddDelivery> {

}