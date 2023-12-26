package com.wanmi.sbc.returnorder.stockupaction.repository;

import com.wanmi.sbc.returnorder.stockupaction.model.entity.StockupAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

/**
 * @description: 提货订单明细持久层接口
 * @author: XinJiang
 * @time: 2021/12/17 15:59
 */
public interface StockupActionRepository extends JpaRepository<StockupAction,Long>, JpaSpecificationExecutor<StockupAction> {

    @Modifying
    @Query("delete from StockupAction where createTime >= ?1 and createTime <= ?2")
    int deleteByCreateTime(LocalDateTime beginTime,LocalDateTime endTime);
}
