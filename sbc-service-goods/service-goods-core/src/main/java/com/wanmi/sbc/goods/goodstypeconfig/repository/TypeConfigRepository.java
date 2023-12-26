package com.wanmi.sbc.goods.goodstypeconfig.repository;

import com.wanmi.sbc.goods.goodstypeconfig.root.RecommendType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * <p>商品推荐商品DAO</p>
 * @author sgy
 * @date 2019-09-07 10:53:36
 */
@Repository
public interface TypeConfigRepository extends JpaRepository<RecommendType, String>,
        JpaSpecificationExecutor<RecommendType> {

    List<RecommendType> findByCompanyInfoId(Long companyInfoId);


    @Modifying
    @Query(value = "delete from merchant_recommend_type where store_cate_id = ?1  and company_info_id = ?2 "   ,nativeQuery = true)
    int deleteByCateAndStoreId(String storeCateId,String companyInfoId);
}
