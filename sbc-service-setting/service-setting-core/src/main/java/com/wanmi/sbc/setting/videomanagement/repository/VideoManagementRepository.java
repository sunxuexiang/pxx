package com.wanmi.sbc.setting.videomanagement.repository;

import com.wanmi.sbc.setting.videomanagement.model.root.VideoManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.wanmi.sbc.common.enums.DeleteFlag;
import java.util.Optional;
import java.util.List;

/**
 * <p>视频管理DAO</p>
 * @author zhaowei
 * @date 2021-04-17 17:47:22
 */
@Repository
public interface VideoManagementRepository extends JpaRepository<VideoManagement, Long>,
        JpaSpecificationExecutor<VideoManagement> {

    /**
     * 单个删除视频管理
     * @author zhaowei
     */
    @Modifying
    @Query("update VideoManagement set delFlag = 1 where videoId = ?1")
    void deleteById(Long videoId);

    /**
     * 批量删除视频管理
     * @author zhaowei
     */
    @Modifying
    @Query("update VideoManagement set delFlag = 1 where videoId in ?1")
    void deleteByIdList(List<Long> videoIdList);

    Optional<VideoManagement> findByVideoIdAndDelFlag(Long id, DeleteFlag delFlag);

    /**
     * 增加播放量
     * @param videoId
     * @return
     */
    @Modifying
    @Query("update VideoManagement set playFew = playFew+1 where videoId = ?1")
    int playFewById(Long videoId);

}
