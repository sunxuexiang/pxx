package com.wanmi.sbc.goods.goodsimagestype.repository;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.goods.goodsimagestype.model.root.GoodsImageStype;
import com.wanmi.sbc.goods.goodslabel.model.root.GoodsLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
public interface GoodsImagestypeRepository extends JpaRepository<GoodsImageStype, Long>,
        JpaSpecificationExecutor<GoodsImageStype> {


    /**
     * 通过goodsInfoid 获取有效图片
     * @param goodsId
     * @return
     */
    @Query(value = "SELECT * from goods_images_type WHERE goods_id = ?1 and del_flag = 0",nativeQuery = true)
    List<GoodsImageStype> findByGoodsId(String goodsId);

    /**
     * 通过goodsInfoid 获取有效图片
     * @param goodsId
     * @return
     */
    @Query(value = "SELECT * from goods_images_type WHERE goods_id in (?1) and del_flag = 0 and type = 1 ",nativeQuery = true)
    List<GoodsImageStype> findByGoodsIdsAndHc(List<String> goodsId);


    /**
     * 批量删除数据
     */
    @Transactional
    @Modifying
    @Query(value = "delete  from  goods_images_type where goods_id = ?1 ",nativeQuery = true)
    int deleteByGoodsInfoId(String goodsId);




}
