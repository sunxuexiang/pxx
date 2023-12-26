package com.wanmi.sbc.returnorder.shopcart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopCartRepository extends JpaRepository<ShopCart, Long>, JpaSpecificationExecutor<ShopCart> {

    /**
     * 根据多个ID编号\分销员id进行删除
     *
     * @param goodsInfoIds 商品ID
     * @param customerId   客户ID
     */
    @Modifying
    @Query("delete from ShopCart p where p.goodsInfoId in ?1 and p.customerId = ?2 and p.inviteeId=?3")
    void deleteByGoodsInfoids(List<String> goodsInfoIds, String customerId, String inviteeId);


    /**
     * 根据多个ID编号\分销员id进行删除
     *
     * @param devanningIds 拆箱id
     * @param customerId   客户ID
     */
    @Modifying
    @Query("delete from ShopCart p where p.devanningId in ?1 and p.customerId = ?2 and p.inviteeId=?3")
    void deleteByDevannings(List<Long> devanningIds, String customerId, String inviteeId);


    /**
     * 根据多个ID编号\分销员id进行删除
     *
     * @param goodsInfoIds 商品ID
     * @param customerId   客户ID
     */
    @Modifying
    @Query("delete from ShopCart p where  p.customerId = ?2 and p.inviteeId=?3 and p.devanningId in ?4")
    void deleteByGoodsInfoidsAAndDevanningId(List<String> goodsInfoIds, String customerId, String inviteeId,List<Long> devannings);


    /**
     * 根据多个ID编号\分销员id进行删除
     *
     * @param customerId   客户ID
     */
    @Modifying
    @Query(value = "delete from shop_cart  where  customer_id = ?1 and invitee_id=?2 and devanning_id in ?3",nativeQuery = true)
    void deleteByGoodsInfoidsAAndDevanningId( String customerId, String inviteeId,List<Long> devannings);

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
    List<ShopCart> queryPurchaseByCustomerIdAndInviteeId(String customerId, String inviteeId);

    /**
     * 根据sku编号和用户编号\分销员id查询采购单列表
     *
     * @param goodsInfoIds
     * @param customerId
     * @return
     */
    @Query("select p from ShopCart p where p.goodsInfoId in (:goodsInfoIds) and p.customerId = :customerId and p.inviteeId=:inviteeId")
    List<ShopCart> queryPurchaseByGoodsIdsAndCustomerId(@Param("goodsInfoIds") List<String> goodsInfoIds, @Param
            ("customerId") String customerId, @Param("inviteeId") String inviteeId);

    /**
     * 获取采购单客户商品种类
     *
     * @param cuostomerId
     * @return
     */
    @Query("SELECT sum(p.goodsNum) from ShopCart p where p.customerId=?1 and p.inviteeId=?2")
    Long queryGoodsNum(String cuostomerId, String inviteeId);

    @Query("SELECT sum(p.goodsNum) from ShopCart p where p.customerId=?1 and p.inviteeId=?2 and p.wareId=?3")
    Long queryGoodsNum(String customerId, String inviteeId,Long wareId);

    /**
     * 获取采购单客户商品种类
     *
     * @param customerId
     * @return
     */
    @Query("SELECT sum(p.goodsNum) from ShopCart p where p.customerId=?1 and p.inviteeId=?2 and p.companyInfoId=?3")
    Long queryGoodsNumByCompanyInfoId(String customerId, String inviteeId,Long companyInfoId);

    /**
     * 统计某用户sku购买的数量
     */
    @Query("select p.goodsInfoId, sum(p.goodsNum) from ShopCart p where p.goodsInfoId in (:goodsInfoIds) and p.customerId = :customerId GROUP BY p.goodsInfoId")
    List<Object[]> querySkuCountGoodsNum(@Param("goodsInfoIds") List<String> goodsInfoIds, @Param("customerId") String customerId);


    /**
     * 根据仓库ID和用户编号查询采购单列表
     *
     * @param wareId
     * @param customerId
     * @return
     */
    @Query("select p from ShopCart p where p.wareId = :wareId and p.customerId = :customerId")
    List<ShopCart> queryPurchaseByWareIdAndCustomerId(@Param("wareId") Long wareId, @Param
            ("customerId") String customerId);
    @Modifying
    void deleteByCartId(Long aLong);
}
