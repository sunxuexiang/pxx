package com.wanmi.sbc.goods.merchantconfig.repository;

import com.wanmi.sbc.goods.merchantconfig.root.MerchantRecommendGoods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * <p>商品推荐商品DAO</p>
 * @author sgy
 * @date 2019-09-07 10:53:36
 */
@Repository
public interface MerchantConfigGoodsRepository extends JpaRepository<MerchantRecommendGoods, String>,
        JpaSpecificationExecutor<MerchantRecommendGoods> {

    /**
     * 通过storeId获取推荐商品信息
     * @param companyInfoId
     * @return
     */
    List<MerchantRecommendGoods> findByCompanyInfoId(Long companyInfoId);

    /**
     * 通过storeId删除推荐商品信息
     * @param companyInfoId
     * @return
     */
    int deleteByCompanyInfoId(Long companyInfoId);
    /**
     * 查询不存在的spu
     * @param companyInfoId
     * @return
     */
    @Query("from MerchantRecommendGoods lg where  lg.companyInfoId =:companyInfoId and lg.goodsInfoId not in (:goodsInfoIds)  ")
    List<MerchantRecommendGoods> notIdList(@Param("companyInfoId") Long companyInfoId, @Param("goodsInfoIds") List<String> goodsInfoIds);
}
