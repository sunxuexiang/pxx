package com.wanmi.sbc.goods.goodstypeconfig.repository;

import com.wanmi.sbc.goods.goodstypeconfig.root.MerchantRecommendType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * <p>商品推荐商品DAO</p>
 * @author sgy
 * @date 2019-09-07 10:53:36
 */
@Repository
public interface MerchantTypeConfigRepository extends JpaRepository<MerchantRecommendType, String>,
        JpaSpecificationExecutor<MerchantRecommendType> {

    /**
     * 通过storeId获取推荐商品信息
     * @param companyInfoId
     * @return
     */
    @Query(value = "SELECT a.merchant_type_id,a.goods_type_id,a.store_id,b.cate_name,b.cate_img,1 " +
            "FROM merchant_recommend_type a LEFT JOIN store_cate b ON a.goods_type_id=b.store_cate_id  " +
            "WHERE a.company_info_id = (:companyInfoId)",nativeQuery = true)
    List<MerchantRecommendType> findByCompanyInfoId(Long companyInfoId);
    /**
     * 通过storeId删除推荐商品信息
     * @param companyInfoId
     * @return
     */
    int deleteByCompanyInfoId(Long companyInfoId);

    @Transactional
    @Modifying
    @Query("update MerchantRecommendType a set a.sort =?2  where a.merchantTypeId = ?1")
    void updateSortById(String id, int sort);
}
