package com.wanmi.sbc.order.inventorydetailsamount.repository;

import com.wanmi.sbc.order.inventorydetailsamount.model.root.InventoryDetailSamount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface InventoryDetailSamountRepository extends JpaRepository<InventoryDetailSamount,Long>, JpaSpecificationExecutor<InventoryDetailSamount> {


    /**
     * 获取没提货的商品
     * @param goodsInfoId
     * @param tid
     * @return
     */
    @Query(value = "SELECT * from inventory_details_amount c  where c.goods_info_id = ?1 and c.new_pile_trade_id = ?2 and c.take_flag = ?3",nativeQuery = true)
    List<InventoryDetailSamount> getBytidAndGoodsInfoId(String goodsInfoId,String tid,int takeFlag);


    /**
     * 获取没提货的商品批量
     * @param takeId
     * @return
     */

    @Transactional
    @Modifying
    @Query(value = "update inventory_details_amount set take_flag = 0 where take_id = ?1 and take_flag = 1",nativeQuery = true)
    int updateInventory(String takeId);

    /**
     * 获取没提货的商品批量
     * @param takeId
     * @return
     */
    @Query(value = "SELECT * from inventory_details_amount c  where c.take_id = ?1",nativeQuery = true)
    List<InventoryDetailSamount> getByTakeId(String takeId);

    /**
     * 获取没提货的商品批量
     * @param takeIds
     * @return
     */
    @Query(value = "SELECT * from inventory_details_amount c  where c.take_id in ?1",nativeQuery = true)
    List<InventoryDetailSamount> getByTakeIds(List<String> takeIds);

    /**
     * 获取没提货的商品批量
     * @param newPileTradeId
     * @return
     */
    @Query(value = "SELECT * from inventory_details_amount c  where c.new_pile_trade_id = ?1 and c.take_flag = ?2",nativeQuery = true)
    List<InventoryDetailSamount> getInventoryByOId(String newPileTradeId,int takeFlag);


    /**
     * 获取没提货的商品批量
     * @param goodsInfoIds
     * @param tid
     * @return
     */
    @Query(value = "SELECT * from inventory_details_amount c  where c.goods_info_id in (?1) and c.new_pile_trade_id = ?2 and c.take_flag = ?3",nativeQuery = true)
    List<InventoryDetailSamount> getBytidAndGoodsInfoIds(List<String> goodsInfoIds,String tid,int takeFlag);

    @Modifying
    @Query(value = "update inventory_details_amount set return_id = ?2,return_type=0,return_flag=0  where new_pile_trade_id = ?1 and take_flag =0 ", nativeQuery = true)
    void updateNewPileReturnRelation(String newPileOrderNo, String returnOrderNo);

    @Modifying
    @Query(value = "update inventory_details_amount set return_id = ?1,return_type=1,return_flag=?3  where inventory_details_amount_id in ?2 ", nativeQuery = true)
    void updateNewPickReturnRelation(String returnId, List<Long> ids, int returnFlag);

    @Modifying
    @Query(value = "update inventory_details_amount set return_id = null,return_type = null  where return_id = ?1 ", nativeQuery = true)
    void unlockAmountByRid(String rid);

    @Modifying
    @Query(value = "update inventory_details_amount set return_flag=1 where return_id = ?1 ", nativeQuery = true)
    void returnAmountByRid(String rid);

    List<InventoryDetailSamount> findAllByNewPileTradeId(String tid);

    List<InventoryDetailSamount> findAllByReturnId(String rid);

    List<InventoryDetailSamount> findAllByNewPileTradeIdIn(List<String> newPileTradeIds);

    List<InventoryDetailSamount> findAllByTakeIdIn(List<String> takeIds);
}
