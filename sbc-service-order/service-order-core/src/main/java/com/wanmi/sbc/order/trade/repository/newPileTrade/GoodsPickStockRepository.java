package com.wanmi.sbc.order.trade.repository.newPileTrade;

import com.wanmi.sbc.order.trade.model.newPileTrade.GoodsPickStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoodsPickStockRepository extends JpaRepository<GoodsPickStock, Long>, JpaSpecificationExecutor<GoodsPickStock> {

    /**
     * 根据商品SKU编号减库存
     * @param stock 库存数
     * @param goodsInfoId 商品ID
     */
    @Modifying
    @Query("update GoodsPickStock w set w.stock = w.stock - ?1, w.updateTime = now() " +
            "where w.goodsInfoId = ?2 and w.stock  >= ?1 and w.newPileTradeNo = ?3")
    int subStockByGoodsInfoIdNewPileTradeNo(Long stock, String goodsInfoId,String newPileTradeNo);

    /**
     * 根据商品SKU编号加库存
     * @param stock 库存数
     * @param goodsInfoId 商品ID
     */
    @Modifying
    @Query(value = "update GoodsPickStock w set w.stock = w.stock + ?1, w.updateTime = now() " +
            "where w.goodsInfoId = ?2 and w.newPileTradeNo = ?3")
    int addStockByGoodsInfoIdNewPileTradeNo(Long stock, String goodsInfoId,String newPileTradeNo);


    @Query(value = "select id,new_pile_trade_no,goods_info_id,goods_info_no,goods_id,state,stock,create_time,update_time,ware_id" +
            " from goods_pick_stock where goods_info_id = ?1 and new_pile_trade_no = ?2",nativeQuery = true)
    GoodsPickStock getGoodsPickStockByGoodsinfoIdAnndNewPileTradeNo(String goodsInfoId, String newPileTradeNo);

    @Query(value = "select id,new_pile_trade_no,goods_info_id,goods_info_no,goods_id,state,stock,create_time,update_time,ware_id from goods_pick_stock " +
            "where state = 1 and stock > 0 and new_pile_trade_no in ?1 and goods_info_id in ?2",nativeQuery = true)
    List<GoodsPickStock> getPileStock(List<String> newPileTradeNos,List<String> goodsInfoIds);

    @Query(value = "select id,new_pile_trade_no,goods_info_id,goods_info_no,goods_id,state,stock,create_time,update_time,ware_id " +
            "from goods_pick_stock where state = 1 and new_pile_trade_no = ?1",nativeQuery = true)
    List<GoodsPickStock> getGoodsPickStockByNewPileTradeNo(String newPileTradeNo);

    @Query(value = "select id,new_pile_trade_no,goods_info_id,goods_info_no,goods_id,state,stock,create_time,update_time,ware_id " +
            "from goods_pick_stock where state = 1 and id != ?1 and new_pile_trade_no = ?2",nativeQuery = true)
    List<GoodsPickStock> getGoodsPickStockByNewPileTradeNoAndId(Long id,String newPileTradeNo);

    @Query(value = "select id,new_pile_trade_no,goods_info_id,goods_info_no,goods_id,state,stock,create_time,update_time,ware_id " +
            "from goods_pick_stock where state = 1 and stock > 0 and new_pile_trade_no in ?1 and ware_id =?2",nativeQuery = true)
    List<GoodsPickStock> findByNewPileTradeNosAndWareId(List<String> newPileTradeNo, Long wareId);

    @Query(value = "select id,new_pile_trade_no,goods_info_id,goods_info_no,goods_id,state,stock,create_time,update_time,ware_id " +
            "from goods_pick_stock where state = 1 and stock > 0 and new_pile_trade_no in ?1 ",nativeQuery = true)
    List<GoodsPickStock> findByNewPileTradeNos(List<String> newPileTradeNo);

    @Modifying
    @Query(value = "update GoodsPickStock w set w.state = 0 where w.newPileTradeNo = ?1 ")
    int updateByNewPileTradeNo(String newPileTradeNo);
}
