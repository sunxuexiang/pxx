package com.wanmi.sbc.goods.customerarealimitdetail.repository;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.goods.cate.model.root.GoodsCate;
import com.wanmi.sbc.goods.customerarealimitdetail.model.root.CustomerAreaLimitDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CustomerAreaLimitDetailRepository extends JpaRepository<CustomerAreaLimitDetail, Long>, JpaSpecificationExecutor<CustomerAreaLimitDetail> {


    /**
     * 获取所有可用的最子分类
     *
     * @return 子分类结果
     */
    @Query(value = "SELECT *\n" +
            "from customer_area_limit_detail t1\n" +
            "WHERE t1.customer_id=?1 and t1.goods_info_id=?2 and t1.region_id in (?3)\n" +
            "and  TO_DAYS(t1.create_time) = TO_DAYS(NOW())",nativeQuery = true)
    List<CustomerAreaLimitDetail> getbycustomerIdAndGoodsInfoidAndRegionId(String customerId,String GoodsInfoid,List<Long> RegionId);

    @Query(value = "SELECT *\n" +
            "from customer_area_limit_detail t1\n" +
            "WHERE t1.customer_id=?1 and t1.goods_info_id=?2 and t1.region_id in (?3)\n" +
            "and  t1.trade_id !=?4\n" +
            "and  TO_DAYS(t1.create_time) = TO_DAYS(NOW())", nativeQuery = true)
    List<CustomerAreaLimitDetail> getbycustomerIdAndGoodsInfoidAndRegionIdAAndTradeIdNot(String customerId,String GoodsInfoid,List<Long> RegionId,String tid);

}
