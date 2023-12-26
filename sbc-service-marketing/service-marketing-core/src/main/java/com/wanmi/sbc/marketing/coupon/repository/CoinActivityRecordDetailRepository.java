package com.wanmi.sbc.marketing.coupon.repository;

import com.wanmi.sbc.marketing.coupon.model.root.CoinActivityRecordDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * 指定商品赠金币
 *
 * @Author : Like
 * @create 2023/5/22 9:27
 */

@Repository
public interface CoinActivityRecordDetailRepository extends JpaRepository<CoinActivityRecordDetail, String>, JpaSpecificationExecutor<CoinActivityRecordDetail> {
    List<CoinActivityRecordDetail> findByOrderNoAndRecordType(String orderNo, Integer recordType);
    List<CoinActivityRecordDetail> findByOrderNoAndGoodsInfoIdInAndRecordType(String orderNo, Collection<String> goodsInfoIds, Integer recordType);
    List<CoinActivityRecordDetail> findByOrderNo(String orderNo);
    List<CoinActivityRecordDetail> findByOrderNoAndGoodsInfoIdIn(String orderNo, Collection<String> GoodsInfoIds);
    List<CoinActivityRecordDetail> findByRecordId(Long recordId);

    @Query(value = "select sum(goods_num) from coin_activity_record_detail where record_type=2 and order_no=?1 and goods_info_id=?2", nativeQuery = true)
    Integer queryGoodsCancelNum(String orderId, String goodsInfoId);

    List<CoinActivityRecordDetail> findByOrderNoInAndRecordType(Collection<String> orderNo, Integer recordType);

}
