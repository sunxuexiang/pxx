package com.wanmi.sbc.goods.standard.repository;

import com.wanmi.sbc.goods.standard.model.root.StandardSku;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品库SKU数据源
 * Created by daiyitian on 2017/04/11.
 */
@Repository
public interface StandardSkuRepository extends JpaRepository<StandardSku, String>, JpaSpecificationExecutor<StandardSku>{

    /**
     * 根据多个商品ID编号进行删除
     * @param goodsIds 商品ID
     */
    @Modifying
    @Query("update StandardSku w set w.delFlag = '1' ,w.updateTime = now() where w.goodsId in ?1")
    void deleteByGoodsIds(List<String> goodsIds);

    /**
     * 根据多个商品ID编号进行删除
     * @param goodsIds 商品ID
     */
    @Modifying
    @Query("update StandardSku w set w.delFlag = '1' ,w.updateTime = now() where w.providerGoodsInfoId in ?1")
    void deleteByProviderGoodsInfoIds(List<String> goodsIds);

    /**
     * 根据商品库sku
     * @param goodsId
     * @return
     */
    @Query
    List<StandardSku> findByGoodsId(String goodsId);

    @Modifying
    @Query("update StandardSku w set w.delFlag = '0' ,w.updateTime = now() where w.delFlag = '1' and w.goodsId = ?1")
    void updateDelFlag(String standardId);

    List<StandardSku> findByGoodsIdIn(List<String> standardGoodsIds);

    @Query("from StandardSku where erpGoodsInfoNo = ?1 and delFlag = 0")
    StandardSku findFirstByErpGoodsInfoNo(String erpNo);

    @Query("from StandardSku where mainSkuGoodsInfoId in ?1 and delFlag = 0")
    List<StandardSku> findSkuListByMainSku(List<String> goodsInfoIdList);

    @Query(value = " FROM StandardSku  WHERE delFlag=0 AND erpGoodsInfoNo in :erpId " )
    List<StandardSku> findByErpGoodsInfoNo(@Param("erpId") List<String> erpId);
}
