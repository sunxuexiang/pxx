package com.wanmi.sbc.returnorder.pilepurchase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 采购单数据源
 * 支持一个人多个购物车
 * Created by sunkun on 2017/11/27.
 */
@Repository
public interface PilePurchaseRepository extends JpaRepository<com.wanmi.sbc.returnorder.pilepurchase.PilePurchase, Long>, JpaSpecificationExecutor<com.wanmi.sbc.returnorder.pilepurchase.PilePurchase> {


    /**
     * 根据多个ID编号\分销员id进行删除
     *
     * @param goodsInfoIds 商品ID
     * @param customerId   客户ID
     */
    @Modifying
    @Query("delete from PilePurchase p where p.goodsInfoId in ?1 and p.customerId = ?2 and p.inviteeId=?3")
    void deleteByGoodsInfoids(List<String> goodsInfoIds, String customerId, String inviteeId);

    /**
     * 根据客户id\分销员id采购单sku数量
     *
     * @param customerId
     * @return
     */
    Integer countByCustomerIdAndInviteeId(String customerId, String inviteeId);

    /**
     * 根据客户id\分销员id\公司信息id查询采购单sku数量
     * @param customerId
     * @param inviteeId
     * @param companyInfoId
     * @return
     */
    Integer countByCustomerIdAndInviteeIdAndCompanyInfoId(String customerId, String inviteeId,Long companyInfoId);

    /**
     * 根据用户id\分销员id查询采购单
     *
     * @param customerId
     * @return
     */
    List<com.wanmi.sbc.returnorder.pilepurchase.PilePurchase> queryPurchaseByCustomerIdAndInviteeId(String customerId, String inviteeId);

    /**
     * 根据sku编号和用户编号\分销员id查询采购单列表
     *
     * @param goodsInfoIds
     * @param customerId
     * @return
     */
    @Query("select p from PilePurchase p where p.goodsInfoId in (:goodsInfoIds) and p.customerId = :customerId and p.inviteeId=:inviteeId")
    List<com.wanmi.sbc.returnorder.pilepurchase.PilePurchase> queryPurchaseByGoodsIdsAndCustomerId(@Param("goodsInfoIds") List<String> goodsInfoIds, @Param
            ("customerId") String customerId, @Param("inviteeId") String inviteeId);

    @Query("select p from PilePurchase p where p.goodsInfoId in (:goodsInfoIds) and p.customerId = :customerId")
    List<com.wanmi.sbc.returnorder.pilepurchase.PilePurchase> queryPurchaseByGoodsInfoIdsAndCustomerId(@Param("goodsInfoIds") List<String> goodsInfoIds, @Param
            ("customerId") String customerId);

    /**
     * 获取采购单客户商品种类
     *
     * @param cuostomerId
     * @return
     */
    @Query("SELECT sum(p.goodsNum) from PilePurchase p where p.customerId=?1 and p.inviteeId=?2")
    Long queryGoodsNum(String cuostomerId, String inviteeId);

    @Query("from PilePurchase p where p.goodsInfoId in ?1")
    List<PilePurchase> queryPilePurchaseInGoodsInfoId(List<String> goodsInfoId);

    /**
     * 获取采购单客户商品种类
     *
     * @param customerId
     * @return
     */
    @Query("SELECT sum(p.goodsNum) from PilePurchase p where p.customerId=?1 and p.inviteeId=?2 and p.companyInfoId=?3")
    Long queryGoodsNumByCompanyInfoId(String customerId, String inviteeId,Long companyInfoId);

    @Query("from PilePurchase p where p.customerId = ?1 and p.goodsId = ?2 and p.goodsInfoId = ?3 order by createTime")
    List<PilePurchase> queryPilePurchase(String customerId,String spu,String sku);

    @Query("from PilePurchase p where p.customerId = ?1 and p.goodsInfoId = ?2 order by createTime")
    List<PilePurchase> getPilePurchase(String customerId,String sku);

    /**
     * 根据客户id和商品skuId获取囤货数量
     * @param customerId
     * @param skuId
     * @return
     */
    @Query("select goodsNum from PilePurchase where customerId = ?1 and goodsInfoId = ?2")
    Long getGoodsNumByCustomerIdAndGoodsInfoId(String customerId,String skuId);

    /**
     * 根据skuId获取囤货总数
     * @param skuId
     * @return
     */
    @Query("select sum(goodsNum) from PilePurchase where goodsInfoId = ?1")
    Long getGoodsNumBySkuId(String skuId);

    /**
     * 根据客户id和商品skuId删除囤货信息
     * @param customerId
     * @param skuId
     * @return
     */
    @Modifying
    @Query("delete from PilePurchase p where p.goodsInfoId = ?2 and p.customerId = ?1")
    int deleteByCustomerIdAndGoodsInfoId(String customerId, String skuId);

    /**
     * 根据客户号和商品skuId修改囤货数量
     * @param customerId
     * @param skuId
     * @param buyCount
     * @return
     */
    @Modifying
    @Query("update PilePurchase set goodsNum = goodsNum - ?3 where goodsInfoId = ?2 and customerId = ?1")
    int updateGoodsNum(String customerId,String skuId,Long buyCount);

    /**
     * 根据客户号获取囤货商品总数
     * @param customerId
     * @return
     */
    @Query("select sum(goodsNum) from PilePurchase where customerId = ?1")
    Long sumGoodsNumByCustomerId(String customerId);

    /**
     * 获取已囤用户
     * @return
     */
    @Query(value = "select e.customer_id as customerId,e.purchase_id as purchaseId from (select se.customer_id,se.purchase_id from pile_purchase se group by se.customer_id) e where e.purchase_id > ?1 ORDER BY e.purchase_id LIMIT 20",nativeQuery = true)
    List<Object> getPilePurchaseCustomerId(Long purchaseId);

    @Query("from PilePurchase se where se.customerId = ?1 order by se.updateTime desc")
    List<PilePurchase> getPilePurchaseByCustomerId(String customerId);

    @Query(value = "select p.goodsInfoId, sum(p.goodsNum) as goodsNumTotal from PilePurchase p where p.goodsInfoId in ?1 group by p.goodsInfoId")
    List<Object> getPileGoodsNumsBuySkuIds(List<String> skuIds);
}
