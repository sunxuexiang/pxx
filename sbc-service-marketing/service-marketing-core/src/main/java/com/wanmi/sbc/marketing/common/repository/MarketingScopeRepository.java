package com.wanmi.sbc.marketing.common.repository;

import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.marketing.common.model.root.Marketing;
import com.wanmi.sbc.marketing.common.model.root.MarketingScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * 营销和商品关联关系
 */
@Repository
public interface MarketingScopeRepository extends JpaRepository<MarketingScope, Long>, JpaSpecificationExecutor<MarketingScope> {
    /**
     * 根据营销编号删除和商品的关联关系
     *
     * @param marketingId
     * @return
     */
    int deleteByMarketingId(Long marketingId);

    /**
     * 根据营销编号查询商品关联
     *
     * @param marketingId
     * @return
     */
    List<MarketingScope> findByMarketingId(Long marketingId);

    /**
     * 根据营销编号和商品skuId
     * @param marketingId
     * @param scopeId
     * @return
     */
    @Query("from MarketingScope m where m.terminationFlag = 0 and m.marketingId = ?1 and m.scopeId = ?2")
    List<MarketingScope> findByMarketingIdAndSkuIds(Long marketingId,String scopeId);

    /**
     * 根据营销编号和skuId查询关联商品信息（终止、未终止）
     * @param marketingId
     * @param scopeId
     * @return
     */
    @Query("from MarketingScope m where m.marketingId = ?1 and m.scopeId = ?2")
    List<MarketingScope> findByMarketingIdAndScopeId(Long marketingId,String scopeId);

    @Query("from MarketingScope m where m.terminationFlag = 0 and m.scopeId in ?1 ")
    List<MarketingScope> findAllByScopeIdIn(List<String>  marketingScopeIds);

    @Query("from MarketingScope m where  m.scopeId in ?1 ")
    List<MarketingScope> findAllByScopeIds(List<String>  marketingScopeIds);

    List<MarketingScope> findByScopeIdIn(List<String>  marketingScopeIds);

    @Query("from MarketingScope m where m.marketingId in ?1 ")
    List<MarketingScope> findAllByMarketingIdIn(List<Long> marketingIds);


    List<MarketingScope> findAllByScopeId(String marketingScopeId);

    @Modifying
    @Query("update MarketingScope set terminationFlag = :terminationFlag where marketingScopeId = :marketingScopeId")
    int terminationMarketingIdAndScopeId(@Param("marketingScopeId")Long marketingScopeId,
                                       @Param("terminationFlag") BoolFlag terminationFlag);

}
