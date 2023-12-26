package com.wanmi.sbc.returnorder.trade.repository;


import com.wanmi.sbc.returnorder.trade.model.root.OrderLogistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 物流信息
 * Created by jinwei on 15/3/2017.
 */
@Repository
@Transactional
public interface OrderLogisticsRepository extends JpaRepository<OrderLogistics, String>,
        JpaSpecificationExecutor<OrderLogistics> {

    @Query(value = "from OrderLogistics e where e.logisticId = ?1")
    OrderLogistics findByLogisticId(String logisticId);


    @Query("from OrderLogistics e where e.logisticId in ?1")
    List<OrderLogistics> findByLogisticIds(List<String> logisticIds);
}

