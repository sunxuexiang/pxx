package com.wanmi.sbc.order.trade.repository;

import com.wanmi.sbc.order.trade.model.entity.PileStockRecordTradeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 提货记录表
 *
 * @author yitang
 * @version 1.0
 */
public interface PileStockRecordTradeItemRepository extends JpaRepository<PileStockRecordTradeItem,Long> {

    /**
     * 今昨日提货金额统计
     * @return
     */
    @Query(value = "SELECT\n" +
            "\t IFNULL( t.createTime, DATE_FORMAT( now( ), '%Y-%m-%d' ) ) createTime,\n" +
            "\tIFNULL( t.price, 0 ) price \n" +
            "FROM\n" +
            "\t(\n" +
            "\t\t( SELECT DATE_FORMAT(create_time,'%Y-%m-%d') createTime,sum( price ) price FROM `sbc-order`.stock_record_trade_item WHERE TO_DAYS( NOW( ) ) - TO_DAYS( create_time ) = 1 ) UNION ALL\n" +
            "\t( SELECT DATE_FORMAT(create_time,'%Y-%m-%d') createTime, sum( price ) price FROM `sbc-order`.stock_record_trade_item WHERE TO_DAYS( NOW( ) ) - TO_DAYS( create_time ) < 1 ) \n" +
            "\t) t",nativeQuery = true)
    List<Object[]> statisticRecordItemPriceByDate();


    /**
     * 七日提货金额统计
     * @return
     */
    @Query(value = "select sum(price*num),DATE_FORMAT(create_time,'%Y-%m-%d') from `sbc-order`.stock_record_trade_item \n" +
            "where create_time >= ?1 and create_time < ?2\n" +
            "group by DATE_FORMAT(create_time,'%Y-%m-%d')",nativeQuery = true)
    List<Object[]> statisticRecordItemPriceBySevenDate(String beginTime,String endTime);

    /**
     * 提货笔数
     * @param beginTime
     * @param endTime
     * @return
     */
    @Query(value = "select count(1) as '提货笔数' from (\n" +
            "select DISTINCT tid from `sbc-order`.stock_record_trade_item where create_time >= ?1 and create_time < ?2)t2",nativeQuery = true)
    List<Object> statisticPileCount(String beginTime,String endTime);

    /**
     * 提货箱数
     * @param beginTime
     * @param endTime
     * @return
     */
    @Query(value = "select sum(num) as '提货箱数' from `sbc-order`.stock_record_trade_item where create_time >= ?1 and create_time < ?2 ",nativeQuery = true)
    List<Object> statisticPileNum(String beginTime,String endTime);

    /**
     * 提货数据
     * @param beginTime
     * @param endTime
     * @return
     */
    @Query(value = "select t2.erp_goods_info_no,t1.sku_name,sum(t1.num) num,sum(t1.num*t1.price) price from `sbc-order`.stock_record_trade_item t1 " +
            "LEFT JOIN `sbc-goods`.goods_info t2 on t1.sku_id = t2.goods_info_id " +
            "where t1.create_time >= ?1 " +
            "and t1.create_time < ?2 " +
            "GROUP BY t1.sku_name,t2.erp_goods_info_no " +
            "order by num desc",nativeQuery = true)
    List<Object[]> statisticPickUpLog(String beginTime,String endTime);
}
