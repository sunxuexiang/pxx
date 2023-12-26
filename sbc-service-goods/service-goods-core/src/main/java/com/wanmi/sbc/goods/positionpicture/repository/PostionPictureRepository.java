package com.wanmi.sbc.goods.positionpicture.repository;


import com.wanmi.sbc.goods.positionpicture.model.root.PositionPicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>积分商品分类表DAO</p>
 * @author yang
 * @date 2019-05-13 09:50:07
 */
@Repository
public interface PostionPictureRepository extends JpaRepository<PositionPicture, Long>,
        JpaSpecificationExecutor<PositionPicture> {

    @Query(value = "select * from position_picture where ware_id = ?1 and del_flag = 0 and type =?2",nativeQuery = true)
    List<PositionPicture> getImageByWareId(Long wareId,Integer type);

    @Query(value = "select * from position_picture where ware_id = ?1 and del_flag = 0",nativeQuery = true)
    List<PositionPicture> getImageByWareId(Long wareId);

    @Query(value = "select * from position_picture where del_flag = 0 and type =?1",nativeQuery = true)
    List<PositionPicture> getAllImage(Integer type);

    @Query(value = "select count(1) from position_picture where del_flag = 0 and store_id = ?1", nativeQuery = true)
    Integer countByStore(Long storeId);

    @Query(value = "select * from position_picture where del_flag = 0 and store_id =?1",nativeQuery = true)
    List<PositionPicture> getImageByStore(Long storeId);

    @Query(value = "select * from position_picture where del_flag = 0 and store_id =?1",nativeQuery = true)
    PositionPicture getOneByStore(Long storeId);

    @Query(value = "select * from position_picture where store_id =?1 and type =?2 and del_flag = 0",nativeQuery = true)
    List<PositionPicture> getImageByStoreAndType(Long storeId, Integer integerType);
}
