package com.wanmi.sbc.returnorder.historytownshiporder.repository;

import com.wanmi.sbc.returnorder.historytownshiporder.model.root.HistoryTownShipOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface HistoryTownShipOrderRepository extends JpaRepository<HistoryTownShipOrder,Long>, JpaSpecificationExecutor<HistoryTownShipOrder> {



    @Query(value = "SELECT\n" +
            "\tsum( t1.stock )- ( CASE WHEN t2.townstock IS NULL THEN 0 ELSE t2.townstock END ) AS stock,\n" +
            "\tt1.goods_info_id \n" +
            "FROM\n" +
            "\t`sbc-goods`.goods_ware_stock t1\n" +
            "\tLEFT JOIN (\n" +
            "\tSELECT\n" +
            "\t\tsum(\n" +
            "\t\tROUND( nt1.num *( CASE WHEN nt1.divisor_flag IS NULL THEN 1 ELSE nt1.divisor_flag END ), 2 )) AS townstock,\n" +
            "\t\tnt1.sku_id \n" +
            "\tFROM\n" +
            "\t\t`sbc-order`.history_town_ship_order nt1 \n" +
            "\tWHERE\n" +
            "\t\tnt1.wms_flag = 0 \n" +
            "\tGROUP BY\n" +
            "\t\tnt1.sku_id \n" +
            "\t) t2 ON t1.goods_info_id = t2.sku_id \n" +
            "\t\n" +
            " inner join `sbc-goods`.goods_info t3 on t1.goods_info_id =t3.goods_info_id\n" +
            "\t\n" +
            "WHERE\n" +
            "\tt1.goods_info_id IN (?1) \n" +
            "\tand t1.del_flag = 0 and t1.ware_id = t3.ware_id\n" +
            "GROUP BY\n" +
            "\tt1.goods_info_id",nativeQuery = true)
    List<Object> getskusstock(List<String> skuids);


    @Query(value = "\t\tSELECT\n" +
            "\t\tsum(\n" +
            "\t\tROUND( nt1.num *( CASE WHEN nt1.divisor_flag IS NULL THEN 1 ELSE nt1.divisor_flag END ), 2 )) AS stock,\n" +
            "\t\tnt1.sku_id as goods_info_id \n" +
            "\tFROM\n" +
            "\t\t`sbc-order`.history_town_ship_order nt1 \n" +
            "\tWHERE\n" +
            "\t\tnt1.wms_flag = 0 and nt1.sku_id in (?1)\n" +
            "\tGROUP BY\n" +
            "\t\tnt1.sku_id ",nativeQuery = true)
    List<Object> getskusJiYastock(List<String> skuids);



    @Query(value = "SELECT\n" +
            "\tsum( t1.stock )- ( CASE WHEN t2.townstock IS NULL THEN 0 ELSE t2.townstock END ) AS stock,\n" +
            "\tt1.goods_info_id \n" +
            "FROM\n" +
            "\t`sbc-goods`.goods_ware_stock t1\n" +
            "\tLEFT JOIN (\n" +
            "\tSELECT\n" +
            "\t\tsum(\n" +
            "\t\tROUND( nt1.num *( CASE WHEN nt1.divisor_flag IS NULL THEN 1 ELSE nt1.divisor_flag END ), 2 )) AS townstock,\n" +
            "\t\tnt1.sku_id \n" +
            "\tFROM\n" +
            "\t\t`sbc-order`.history_town_ship_order nt1 \n" +
            "\tWHERE\n" +
            "\t\tnt1.wms_flag = 0 \n" +
            "\tGROUP BY\n" +
            "\t\tnt1.sku_id \n" +
            "\t) t2 ON t1.goods_info_id = t2.sku_id \n" +
            "\t\n" +
            " inner join `sbc-goods`.retail_goods_info t3 on t1.goods_info_id =t3.goods_info_id\n" +
            "\t\n" +
            "WHERE\n" +
            "\tt1.goods_info_id IN (?1) \n" +
            "\tand t1.del_flag = 0 and t1.ware_id = t3.ware_id\n" +
            "GROUP BY\n" +
            "\tt1.goods_info_id",nativeQuery = true)
    List<Object> getskusstockbylingshou(List<String> skuids);








    @Query(value = "SELECT\n" +
            "\tsum( t1.stock )- ( CASE WHEN t2.townstock IS NULL THEN 0 ELSE t2.townstock END ) AS stock,\n" +
            "\tt1.goods_info_id \n" +
            "FROM\n" +
            "\t`sbc-goods`.goods_ware_stock t1\n" +
            "\tLEFT JOIN (\n" +
            "\tSELECT\n" +
            "\t\tsum(\n" +
            "\t\tROUND( nt1.num *( CASE WHEN nt1.divisor_flag IS NULL THEN 1 ELSE nt1.divisor_flag END ), 2 )) AS townstock,\n" +
            "\t\tnt1.sku_id \n" +
            "\tFROM\n" +
            "\t\t`sbc-order`.history_town_ship_order nt1 \n" +
            "\tWHERE\n" +
            "\t\tnt1.wms_flag = 0 \n" +
            "\tGROUP BY\n" +
            "\t\tnt1.sku_id \n" +
            "\t) t2 ON t1.goods_info_id = t2.sku_id \n" +
            "\t\n" +
            " inner join `sbc-goods`.bulk_goods_info t3 on t1.goods_info_id =t3.goods_info_id\n" +
            "\t\n" +
            "WHERE\n" +
            "\tt1.goods_info_id IN (?1) \n" +
            "\tand t1.del_flag = 0 and t1.ware_id = t3.ware_id\n" +
            "GROUP BY\n" +
            "\tt1.goods_info_id",nativeQuery = true)
    List<Object> getskusstockbybulk(List<String> skuids);







    @Modifying
    @Query(value = "update history_town_ship_order c set c.wms_flag = 1 where c.tid = ?1",nativeQuery = true)
    int updateWmsFlag(String tid);

    @Modifying
    @Query(value = "update history_town_ship_order c set c.cancel_flag = 1 where c.tid = ?1",nativeQuery = true)
    int updateThWmsFlag(String tid);

    @Query(value = "SELECT * from history_town_ship_order c  where c.tid = ?1 and c.wms_flag = 0",nativeQuery = true)
    List<HistoryTownShipOrder> getOrderBytid(String tid);

    /**
     * 查询已经推送并且未取消的订单通过订单编号查询
     * @param tid
     * @return
     */
    @Query(value = "SELECT * from history_town_ship_order c  where c.tid = ?1 and c.wms_flag = 1 and c.cancel_flag = 0",nativeQuery = true)
    List<HistoryTownShipOrder> getTsOrderBytid(String tid);
}
