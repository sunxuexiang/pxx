package com.wanmi.sbc.order.trade.repository.newPileTrade;

import com.wanmi.sbc.order.trade.model.newPileTrade.GoodsPickStock;
import com.wanmi.sbc.order.trade.model.newPileTrade.NewPileOldData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NewPileOldDataRepository extends JpaRepository<NewPileOldData, Integer>, JpaSpecificationExecutor<NewPileOldData> {


    @Query(value = "SELECT\n" +
            " p.customer_id as customerId,\n" +
            " p.goods_info_id as goodsInfoId,\n" +
            " c.customer_account as account,#账号\n" +
            " i.goods_info_name as goodsName,#参考商品名称\n" +
            " i.erp_goods_info_no as erpNo,#参考物料编码\n" +
            " i.market_price as price,#参考商品金额\n" +
            " p.goods_num as goodsNum,#数量 \n" +
            " c.province_name as province,#省\n" +
            " c.city_id as city,#市\n" +
            " c.area_name as area, #区\n" +
            " c.delivery_address as address,#详细地址\n" +
            " p.create_time as createTime\n" +
            "FROM\n" +
            " `sbc-order`.pile_purchase p\n" +
            "LEFT JOIN `sbc-goods`.goods_info i ON p.goods_info_id = i.goods_info_id\n" +
            "LEFT JOIN (\n" +
            " SELECT\n" +
            "  c.customer_id Customer_id,\n" +
            "  c.customer_account Customer_account,\n" +
            "  a.consignee_number,\n" +
            "  a.province_name,\n" +
            "  a.city_id,\n" +
            "  a.area_name,\n" +
            "  a.twon_name,\n" +
            "  a.delivery_address\n" +
            " FROM\n" +
            "  `sbc-customer`.customer c\n" +
            " LEFT JOIN (\n" +
            "  SELECT\n" +
            "   MAX(customer_id) Customer_id,\n" +
            "   consignee_number,\n" +
            "   province_name,\n" +
            "   city_id,\n" +
            "   area_name,\n" +
            "   twon_name,\n" +
            "   delivery_address\n" +
            "  FROM\n" +
            "   `sbc-customer`.customer_delivery_address\n" +
            "  WHERE\n" +
            "   del_flag = 0\n" +
            "  AND province_name IS NOT NULL\n" +
            "  GROUP BY\n" +
            "   customer_id\n" +
            " ) a ON a.Customer_id = c.customer_id\n" +
            ") c ON p.customer_id = c.Customer_id\n" +
            "WHERE\n" +
            " p.goods_num != 0",nativeQuery = true)
    List<Object> queryAllNewPileOldData();

    @Override
    <S extends NewPileOldData> List<S> saveAll(Iterable<S> entities);


    /**
     * 更新订单取消
     * @param account
     * @param goodsInfoId
     */
    @Transactional
    @Modifying
    @Query(value = "UPDATE new_pile_old_data set state = 1 WHERE account = ?1 and goods_info_id = ?2 ",nativeQuery = true)
    void updateStatus(String account, String goodsInfoId);

    /**
     * 更新订单取消
     * @param account
     */
    @Transactional
    @Modifying
    @Query(value = "UPDATE new_pile_old_data set state = 1 WHERE account = ?1 ",nativeQuery = true)
    void updateByAccountStatus(String account);
}
