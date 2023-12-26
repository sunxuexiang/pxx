package com.wanmi.sbc.returnorder.trade.repository;

import com.wanmi.sbc.returnorder.trade.model.entity.PileStockRecordAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 提货单与囤货单关联
 *
 * @author yitang
 * @version 1.0
 */
public interface PileStockRecordAttachmentRepostory extends JpaRepository<PileStockRecordAttachment,Long> {

    @Query(value = "select * from stock_record_trade_item_attachment s where s.tid = ?1 and s.sku_id in (?2) ORDER BY s.price asc ",nativeQuery = true)
    List<PileStockRecordAttachment> findStockRecordTidAndSkuid(String tid,List<String> skuIds);

    /**
     * 依据提货单查询囤货单
     * @param tid
     * @return
     */
    @Query(value = "select s.order_code as orderCode,s.tid as tid from stock_record_trade_item_attachment s where s.tid in (?1) GROUP BY s.order_code,s.tid",nativeQuery = true)
    List<Object> findStockAssociatedOrderTid(List<String> tid);

    /**
     * 依据囤货单查询提货单
     * @param orderCode
     * @return
     */
    @Query(value = "select s.order_code as orderCode,s.tid as tid from stock_record_trade_item_attachment s where s.order_code in (?1) GROUP BY s.tid,s.order_code",nativeQuery = true)
    List<Object> findStockAssociatedOrderCode(List<String> orderCode);

    /**
     * 根据提货订单获取关联数据
     * @param tid
     * @return
     */
    @Query("from PileStockRecordAttachment t where t.tid = ?1")
    List<PileStockRecordAttachment> findStockRecordAttachment(String tid);

    /**
     * 通过提货单和商品获取关联数据(通过价格倒序)
     * @param tid
     * @param skuIds
     * @return
     */
    @Query(value = "select * from stock_record_trade_item_attachment s where s.tid = ?1 and s.sku_id in (?2) ORDER BY s.price desc ",nativeQuery = true)
    List<PileStockRecordAttachment> findStockRecordTidAndSkuidPriceReverse(String tid,List<String> skuIds);
}
