package com.wanmi.sbc.setting.videoresource.repository;

import com.wanmi.sbc.setting.videoresource.model.root.VideoResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 视频教程资源库DAO
 * @author hudong
 * @date 2023-06-26 09:12:49
 */
@Repository
public interface VideoResourceRepository extends JpaRepository<VideoResource, Long>,
        JpaSpecificationExecutor<VideoResource> {

    /**
     * 单个删除店铺资源库
     * @author lq
     */
    @Modifying
    @Query("update VideoResource set delFlag = 1 where resourceId = ?1")
    void deleteById(Long resourceId);

    /**
     * 批量删除店铺资源库
     * @author lq
     */
    @Modifying
    @Query("update VideoResource set delFlag = 1 ,updateTime = now() where resourceId in ?1  and storeId = ?2")
    int deleteByIdList(List<Long> resourceIdList, Long storeId);

    /**
     * 根据多个ID编号更新分类
     *
     * @param cateId   分类ID
     * @param resourceIds 素材ID
     */
    @Modifying
    @Query("update VideoResource w set w.cateId = ?1 ,w.updateTime = now() where w.resourceId in ?2 and w.storeId = ?3")
    void updateCateByIds(String cateId, List<Long> resourceIds, Long storeId);

    /**
     * 根据多个分类编号更新分类
     *
     * @param cateId   分类ID
     * @param cateIds 分类ID
     */
    @Modifying
    @Query("update VideoResource w set w.cateId = ?1 ,w.updateTime = now() where w.cateId in ?2")
    void updateCateByCateIds(String cateId, List<String> cateIds);
    @Query("from VideoResource  where cateId in ?1 and delFlag = 0")
    List<VideoResource> findByIdList(List<String> cateIdList);


}
