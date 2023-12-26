package com.wanmi.sbc.goods.mapping.repository;

import com.wanmi.sbc.goods.mapping.model.root.GoodsInfoMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface GoodsInfoMappingRepository extends JpaRepository<GoodsInfoMapping, String>, JpaSpecificationExecutor<GoodsInfoMapping> {


    GoodsInfoMapping findByErpGoodsInfoNo(String erpGoodsInfoNo);

    @Query(value = " select * from goods_info_mapping WHERE erp_goods_info_no like CONCAT('%',?1) ", nativeQuery = true)
    List<GoodsInfoMapping> findByErpGoodsInfoNoLike(String erpGoodsInfoNo);

    @Transactional
    @Modifying
    @Query(value = " insert into goods_info_mapping (erp_goods_info_no, goods_info_id, ware_id, parent_goods_info_id) values (:#{#goodsInfoMapping.erpGoodsInfoNo}, :#{#goodsInfoMapping.goodsInfoId}, :#{#goodsInfoMapping.wareId}, :#{#goodsInfoMapping.parentGoodsInfoId}) ", nativeQuery = true)
    Integer saveGoodsInfoMapping(@Param("goodsInfoMapping") GoodsInfoMapping goodsInfoMapping);

    @Transactional
    @Modifying
    @Query(value = " update goods_info_mapping set goods_info_id = ?1 where erp_goods_info_no = ?2 ", nativeQuery = true)
    void updateGoodsInfoIdByErpNo(String goodsInfoId, String erpGoodsInfoNo);

    List<GoodsInfoMapping> findByGoodsInfoIdIn(List<String> goodsInfoIds);

    @Query(value = " select * from goods_info_mapping WHERE parent_goods_info_id in (?1) and ware_id = ?2", nativeQuery = true)
    List<GoodsInfoMapping> findByParentGoodsInfoIdInAndWareId(List<String> goodsInfoIds,Long wareId);

    @Query(value = " select * from goods_info_mapping where parent_goods_info_id in " +
            "(select parent_goods_info_id from  goods_info_mapping where goods_info_id in (?1))" +
            " and ware_id = ?2 ", nativeQuery = true)
    List<GoodsInfoMapping> findWareId(List<String> goodsInfoIds,Long wareId);
}
