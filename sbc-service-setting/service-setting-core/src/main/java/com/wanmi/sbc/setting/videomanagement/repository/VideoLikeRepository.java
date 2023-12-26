package com.wanmi.sbc.setting.videomanagement.repository;

import com.wanmi.sbc.setting.videomanagement.model.root.VideoLike;
import io.lettuce.core.dynamic.annotation.Param;
import io.lettuce.core.protocol.CommandHandler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>视频管理DAO</p>
 *
 * @author zhaowei
 * @date 2021-04-19 14:24:26
 */
@Repository
public interface VideoLikeRepository extends JpaRepository<VideoLike, Long>,
        JpaSpecificationExecutor<VideoLike> {

    @Query("SELECT count(videoId) from VideoLike where videoId = ?1 ")
    Long countByVideoId(@Param("videoId") long videoId);

    @Query("SELECT count(videoId) from VideoLike where videoId = ?1 and customerId = ?2 ")
    Integer queryByIdOrCustomerId(@Param("videoId") long videoId, @Param("customerId") String customerId);

    /**
     * 点赞
     *
     * @return: int
     */
    @Modifying
    @Query(value = "insert into video_like(video_id,customer_id) values(?1,?2)", nativeQuery = true)
    int saveVideoLike(@Param("videoId") long videoId, @Param("customerId") String customerId);

    /**
     * 取消点赞
     *
     * @return: int
     */
    @Modifying
    @Query("delete from VideoLike c where c.videoId = ?1 and c.customerId = ?2 ")
    int deleteVideoLike(@Param("videoId") long videoId, @Param("customerId") String customerId);
}
