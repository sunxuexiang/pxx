package com.wanmi.sbc.returnorder.pilepurchaseaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PilePurchaseActionRepository extends JpaRepository<PilePurchaseAction,Long>, JpaSpecificationExecutor<PilePurchaseAction> {

    @Query("from PilePurchaseAction p where p.purchaseId = ?1 order by create_time asc")
    List<PilePurchaseAction> queryByPurchaseId(Long purchaseId);

    @Query("from PilePurchaseAction p where p.goodsInfoId in ?1")
    List<PilePurchaseAction> queryPilePurchaseActionInGoodsInfoId(List<String> goodsInfoId);

    @Query(value = "select * from pile_purchase_action where pid is null limit 1000",nativeQuery = true)
    List<PilePurchaseAction> getPilePurchaseActions();

    /**
     * 囤货金额及件数统计
     * @return
     */
    @Query(value = "select sum(goods_split_price*goods_num),sum(goods_num),DATE_FORMAT(create_time,'%Y-%m-%d') from `sbc-order`.pile_purchase_action \n" +
            "where create_time >= ?1 and create_time < ?2\n" +
            "group by DATE_FORMAT(create_time,'%Y-%m-%d')",nativeQuery = true)
    List<Object[]> statisticRecordItemPrice(String beginTime,String endTime);

    /**
     * 囤货笔数
     * @param beginTime
     * @param endTime
     * @return
     */
    @Query(value = "select count(1) as '囤货笔数' from (\n" +
            "select DISTINCT order_code from `sbc-order`.pile_purchase_action where create_time >= ?1 and create_time < ?2)t1",nativeQuery = true)
    List<Object> statisticRecordItemCount(String beginTime,String endTime);

    /**
     * 囤货金额、囤货件数
     * @param beginTime
     * @param endTime
     * @return
     */
    @Query(value = "select  ifnull(sum(goods_split_price*goods_num),0) as '囤货金额',ifnull(sum(goods_num),0) as '囤货件数' from `sbc-order`.pile_purchase_action  where create_time >= ?1 and create_time < ?2 ",nativeQuery = true)
    List<Object[]> statisticRecordItemPriceNum(String beginTime,String endTime);


    /**
     * 囤货未提数据，实时
     * @return
     */
    @Query(value = " select ifnull(t2.erp_goods_info_no,''),ifnull(t2.goods_info_name,''),ifnull(sum(goods_num),0) from " +
            "`sbc-order`.pile_purchase t1 LEFT JOIN `sbc-goods`.goods_info t2 on t1.goods_info_id = t2.goods_info_id  \n" +
            "where goods_num > 0   group by t2.erp_goods_info_no,t2.goods_info_name",nativeQuery = true)
    List<Object[]> statisticRecordItemPriceNumNoPile();

    /**
     * 囤货未提数据，实时
     * @return
     */
    @Query(value = "select ifnull(sum(t1.goods_num),0),ifnull(t2.customer_account,''),ifnull(t5.consignee_name,''),ifnull(t4.employee_name,'')   \n" +
            "from `sbc-order`.pile_purchase t1 \n" +
            "left join `sbc-customer`.customer t2 on t1.customer_id = t2.customer_id\n" +
            "left join `sbc-customer`.customer_detail t3 on t1.customer_id = t3.customer_id\n" +
            "left join `sbc-customer`.employee t4 on t3.employee_id = t4.employee_id\n" +
            "left join  (select DISTINCT customer_id,consignee_name from `sbc-customer`.customer_delivery_address) t5\n" +
            "on t1.customer_id = t5.customer_id\n" +
            "where t1.goods_num > 0  \n" +
            "group by t2.customer_account,t4.employee_name,t5.consignee_name",nativeQuery = true)
    List<Object[]> statisticRecordItemPriceNumNoPileUser();
    /**
     * 七日囤货金额及件数统计
     * @return
     */
    @Query(value = "select sum(goods_split_price*goods_num),sum(goods_num),DATE_FORMAT(create_time,'%Y-%m-%d') from `sbc-order`.pile_purchase_action \n" +
            "where create_time >= ?1 and create_time < ?2\n" +
            "group by DATE_FORMAT(create_time,'%Y-%m-%d')",nativeQuery = true)
    List<Object[]> statisticRecordItemPriceBySevenDate(String beginTime,String endTime);
}
