package com.wanmi.sbc.marketing.marketingpurchaselimit.repository;

import com.wanmi.sbc.marketing.marketingpurchaselimit.model.root.MarketingPurchaseLimit;
import com.wanmi.sbc.marketing.pointscoupon.model.root.PointsCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * <p>积分兑换券表DAO</p>
 *
 * @author yang
 * @date 2019-06-11 10:07:09
 */
@Repository
public interface MarketingPurchaseLimitRepository extends JpaRepository<MarketingPurchaseLimit, Long>,
        JpaSpecificationExecutor<MarketingPurchaseLimit> {

    @Modifying
    @Query(value = "SELECT * from  marketing_purchase_limit t1\n" +
            "WHERE t1.customer_id = ?1 and t1.marketing_id = ?2 and t1.goods_info_id=?3",nativeQuery = true)
    List<MarketingPurchaseLimit> getbyCoutomerIdAndMarketingIdAndGoodsInfoId(String customerId,Long marketingId,String goodsInfoId);



    @Modifying
    @Query(value = "SELECT * from  marketing_purchase_limit t1\n" +
            "WHERE  t1.marketing_id = ?1 and t1.goods_info_id=?2",nativeQuery = true)
    List<MarketingPurchaseLimit> getbyMarketingIdAndGoodsInfoId(Long marketingId,String goodsInfoId);

    @Modifying
    @Query(value = "SELECT * from  marketing_purchase_limit t1\n" +
            "WHERE  t1.marketing_id = ?2 and t1.customer_id=?1",nativeQuery = true)
    List<MarketingPurchaseLimit> getbyCustomerIdAndMarketingId(String customerId, Long marketingId);
}
