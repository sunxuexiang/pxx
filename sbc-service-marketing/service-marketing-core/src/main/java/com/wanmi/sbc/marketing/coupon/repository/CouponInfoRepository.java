package com.wanmi.sbc.marketing.coupon.repository;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.marketing.coupon.model.root.CouponInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @Author: songhanlin
 * @Date: Created In 11:39 AM 2018/9/12
 * @Description: 优惠券信息Repository
 */
@Repository
public interface CouponInfoRepository extends JpaRepository<CouponInfo, String>, JpaSpecificationExecutor<CouponInfo> {

    @Modifying
    @Query("from CouponInfo w where w.delFlag = 0 and w.couponId in ?1")
    List<CouponInfo> queryByIds(List<String> ids);

    @Query("from CouponInfo w where w.delFlag = 0 and w.couponId = ?1")
    CouponInfo queryById(String couponId);

    Optional<CouponInfo> findByCouponIdAndStoreIdAndDelFlag(String couponId, Long storeId, DeleteFlag deleteFlag);

    @Modifying
    @Query("update CouponInfo a set a.delFlag = 1 ,a.delTime = now() ,a.delPerson = ?2 where a.couponId = ?1")
    int deleteCoupon(String id, String operatorId);

    @Query(value = "select * from coupon_info t1 where t1.store_id in ?1 and t1.del_flag = 0",nativeQuery = true)
    List<CouponInfo> queryBystoreIds(List<String> storeIdList);

}
