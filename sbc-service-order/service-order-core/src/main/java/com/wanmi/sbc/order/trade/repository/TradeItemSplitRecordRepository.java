package com.wanmi.sbc.order.trade.repository;

import com.wanmi.sbc.order.trade.model.root.TradeItemSplitRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @description trade_item_split_record
 * @author shiy
 * @date 2023-04-08
 */
public interface TradeItemSplitRecordRepository extends JpaRepository<TradeItemSplitRecord, String> {

    /**
     * @description  根据订单号查拆分的数据
     * @author  shiy
     * @date    2023/4/11 17:10
     * @params  [java.lang.String]
     * @return  java.util.List<com.wanmi.sbc.order.trade.model.root.TradeItemSplitRecord>
    */
    @Query(value = "select * from trade_item_split_record sr where sr.trade_no = ?1 AND back_flag=0",nativeQuery = true)
    List<TradeItemSplitRecord> findListByTradeNo(String tradeNo);

    @Modifying
    @Query("update TradeItemSplitRecord s set s.backFlag = 1 where s.id=?1")
    int updateBackFlagById(Long id);
}
