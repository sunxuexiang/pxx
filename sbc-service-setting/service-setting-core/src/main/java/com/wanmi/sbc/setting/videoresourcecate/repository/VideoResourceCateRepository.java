package com.wanmi.sbc.setting.videoresourcecate.repository;

import com.wanmi.sbc.setting.videoresourcecate.model.root.VideoResourceCate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>店铺资源资源分类表DAO</p>
 * @author lq
 * @date 2019-11-05 16:13:19
 */
@Repository
public interface VideoResourceCateRepository extends JpaRepository<VideoResourceCate, Long>,
        JpaSpecificationExecutor<VideoResourceCate> {

    /**
     * 单个删除店铺资源资源分类表
     * @author lq
     */
    @Modifying
    @Query("update VideoResourceCate set delFlag = 1 where cateId = ?1")
    void deleteById(String cateId);

    /**
     * 批量删除店铺资源资源分类表
     * @author lq
     */
    @Modifying
    @Query("update VideoResourceCate set delFlag = 1 where cateId in ?1")
    int deleteByIdList(List<String> cateIdList);

    /**
     * 查询
     * @param cateId 素材分类id
     * @param storeId 店铺id
     * @return
     */
    VideoResourceCate findByCateIdAndStoreId(String cateId, Long storeId);

    /**
     * 查询
     * @param cateId 素材分类id
     * @return
     */
    VideoResourceCate findByCateId(String cateId);

    /**
     * 批量查询视频资源资源分类表
     * @author hd
     */
    @Query("from VideoResourceCate  where cateId in ?1 and delFlag = 0")
    List<VideoResourceCate> findByIdList(List<String> cateIdList);


}
