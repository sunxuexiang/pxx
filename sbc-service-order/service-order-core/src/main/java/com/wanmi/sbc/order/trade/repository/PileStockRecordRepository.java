package com.wanmi.sbc.order.trade.repository;

import com.wanmi.sbc.order.trade.model.entity.PileStockRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 囤货记录
 *
 * @author yitang
 * @version 1.0
 */
public interface PileStockRecordRepository extends JpaRepository<PileStockRecord,Long> {

    @Query(value = "select * from stock_record sr where sr.customer_id = ?2 and sr.goods_info_id in (?1)" +
            " and sr.is_use = 0 order by sr.create_time asc",nativeQuery = true)
    List<PileStockRecord> findPileStockRecord(List<String> stockRecordSkuIds, String customerId);

    @Query(value = "select * from stock_record re where re.order_code = ?1 and re.goods_info_id in (?2) and re.is_use = 0 ",nativeQuery = true)
    List<PileStockRecord> getPileStockRecordByOrderCode(String orderCode,List<String> stockRecordSkuIds);

    @Query(value = "select * from stock_record sr where sr.is_use = 0 and sr.customer_id = ?1 " +
            " and sr.goods_info_id in (?2) order by sr.create_time asc",nativeQuery = true)
    List<PileStockRecord> findSingleBackPileStockRecord(String customerId,List<String> goodsInfoId);

    @Query("from PileStockRecord r where r.orderCode=?1")
    List<PileStockRecord> getStockRecordOrderCode(String orderCode);

    /**
     * 囤货单对应商品
     * @param orderCode
     * @param stockRecordSkuIds
     * @return
     */
    @Query(value = "select * from stock_record re where re.order_code = ?1 and re.goods_info_id in (?2)",nativeQuery = true)
    List<PileStockRecord> getPileStockRecordByOrderCodeAll(String orderCode,List<String> stockRecordSkuIds);

    /**
     * 今昨日囤货金额统计
     * @return
     */
    @Query(value = "SELECT\n" +
            "\t IFNULL( t.createTime, DATE_FORMAT( now( ), '%Y-%m-%d' ) ) createTime,\n" +
            "\t IFNULL( t.stockRecordPrice, 0 ) price \n" +
            "FROM\n" +
            "\t(\n" +
            "\t\t( SELECT DATE_FORMAT(create_time,'%Y-%m-%d') createTime,sum( stock_record_price ) stockRecordPrice FROM `sbc-order`.stock_record WHERE TO_DAYS( NOW( ) ) - TO_DAYS( create_time ) = 1 ) UNION ALL\n" +
            "\t( SELECT DATE_FORMAT(create_time,'%Y-%m-%d') createTime,sum( stock_record_price ) stockRecordPrice FROM `sbc-order`.stock_record WHERE TO_DAYS( NOW( ) ) - TO_DAYS( create_time ) < 1 ) \n" +
            "\t) t",nativeQuery = true)
    List<Object[]> statisticRecordPriceByDate();
}
