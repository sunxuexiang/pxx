package com.wanmi.sbc.order.orderpicking.repository;

import com.wanmi.sbc.order.orderpicking.model.root.OrderPicking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/4/12 14:32
 */
@Repository
public interface OrderPickingRepository extends JpaRepository<OrderPicking,Long>, JpaSpecificationExecutor<OrderPicking> {

    Optional<OrderPicking> findByTradeId(String tid);

    /**
     * 根据tid查询列表
     * @param tidList
     * @return
     */
    @Query("from OrderPicking o where o.tradeId in :tidList")
    List<OrderPicking> getByTidList(@Param("tidList") List<String> tidList);
}
