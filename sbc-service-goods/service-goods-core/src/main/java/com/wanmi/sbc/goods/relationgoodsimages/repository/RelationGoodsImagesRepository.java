package com.wanmi.sbc.goods.relationgoodsimages.repository;

import com.wanmi.sbc.goods.goodsimagestype.model.root.GoodsImageStype;
import com.wanmi.sbc.goods.relationgoodsimages.model.root.RelationGoodsImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface RelationGoodsImagesRepository extends JpaRepository<RelationGoodsImages, Long>,
        JpaSpecificationExecutor<RelationGoodsImages> {



    /**
     * goodsId 获取关联关系
     * @param goodsId
     * @return
     */
    @Query(value = "SELECT * from relation_goods_images WHERE goods_id = ?1 and del_flag = 0",nativeQuery = true)
    List<RelationGoodsImages> findByGoodsId(String goodsId);


    @Transactional
    @Modifying
    @Query(value = "delete  from  relation_goods_images where goods_id = ?1 ",nativeQuery = true)
    int deleteByGoodsInfoId(String goodsId);


}
