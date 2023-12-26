package com.wanmi.sbc.setting.banneradmin.repository;

import com.wanmi.sbc.setting.banneradmin.model.root.BannerAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.wanmi.sbc.common.enums.DeleteFlag;
import java.util.Optional;
import java.util.List;

/**
 * <p>轮播管理DAO</p>
 * @author 费传奇
 * @date 2020-12-08 11:44:38
 */
@Repository
public interface BannerAdminRepository extends JpaRepository<BannerAdmin, Long>,
        JpaSpecificationExecutor<BannerAdmin> {

    /**
     * 单个删除轮播管理
     * @author 费传奇
     */
    @Modifying
    @Query("update BannerAdmin set delFlag = 1 where id = ?1")
    void deleteById(Long id);

    /**
     * 批量删除轮播管理
     * @author 费传奇
     */
    @Modifying
    @Query("update BannerAdmin set delFlag = 1 where id in ?1")
    void deleteByIdList(List<Long> idList);

    Optional<BannerAdmin> findByIdAndDelFlag(Long id, DeleteFlag delFlag);

    /**
     * 查询分类下的所有数据
     * @param id
     * @param delFlag
     * @return
     */
    List<BannerAdmin> findByOneCateIdAndDelFlagOrderByBannerSortAsc(Long id, DeleteFlag delFlag);

    /**
     * 查询该分类存在多少条数据
     * @param oneCateId
     * @return
     */
    @Query("select count (b.oneCateId) from BannerAdmin b where b.delFlag = 0 and b.oneCateId = ?1")
    Long findBannerAdminByOneCateId(Long oneCateId);

}
