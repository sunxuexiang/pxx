package com.wanmi.sbc.goods.images;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品图片数据源 Created by daiyitian on 2017/04/11.
 */
@Repository
public interface GoodsImageRepository extends JpaRepository<GoodsImage, Long> {

	/**
	 * 根据商品ID查询
	 * 
	 * @param goodsId 商品ID
	 * @return 商品图片信息
	 */
	@Query("from GoodsImage w where w.delFlag = '0' and w.goodsId = ?1")
	List<GoodsImage> findByGoodsId(String goodsId);

	/**
	 * 根据商品ID批量查询
	 * 
	 * @param goodsIds 商品ID
	 * @return 商品图片信息
	 */
	@Query("from GoodsImage w where w.delFlag = '0' and w.goodsId in ?1")
	List<GoodsImage> findByGoodsIds(List<String> goodsIds);

	/**
	 * 根据水印图查询
	 * 
	 * @return
	 */
	@Query(value = 
			"SELECT gi.image_id AS imageId, gi.artwork_url AS artworkUrl FROM goods_image gi WHERE gi.del_flag = 0 AND gi.watermark_url IS NULL LIMIT 100", 
			nativeQuery = true)
	List<Object> findByWatermark();
	
	/**
	 * 修改水印图
	 * @param watermarkUrl
	 * @param imageId
	 * @return
	 */
	@Modifying
	@Query(value = "UPDATE goods_image gi SET gi.watermark_url = :watermarkUrl WHERE gi.image_id = :imageId", nativeQuery = true)
	int updateWatermarkBy(@Param("watermarkUrl") String watermarkUrl, @Param("imageId") Long imageId);

}
