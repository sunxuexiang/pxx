package com.wanmi.sbc.setting.videomanagement.repository;

import com.wanmi.sbc.setting.videomanagement.model.root.VideoFollow;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VideoFollowRepository extends JpaRepository<VideoFollow,Long> , JpaSpecificationExecutor<VideoFollow> {

    /**
     * 新增关注信息
     * @param followCustomerId
     * @param coverFollowCustomerId
     * @return
     */
    @Modifying
    @Query(value = "INSERT INTO video_follow (follow_customer_id,cover_follow_customer_id,store_id) VALUES (?1,?2,?3)", nativeQuery = true)
    int saveVideoFollow(@Param("followCustomerId") String followCustomerId, @Param("coverFollowCustomerId") String coverFollowCustomerId, @Param("storeId") Long storeId);

    /**
     * 查询我关注的所有人
     * @param followCustomerId
     * @return
     */
    @Query("SELECT storeId FROM VideoFollow WHERE followCustomerId = ?1 ")
    List<Long> getVideoFollowByFollowCustomerId(@Param("followCustomerId") String followCustomerId);

    /**
     * 取消收藏
     * @param followCustomerId
     * @param storeId
     */
    @Modifying
    @Query("DELETE FROM VideoFollow f WHERE f.followCustomerId = ?1 AND f.storeId = ?2 ")
    int deleteVideoFollow(@Param("followCustomerId") String followCustomerId, @Param("storeId") Long storeId);


    @Query("SELECT count(*) FROM VideoFollow f WHERE f.followCustomerId = ?1 AND f.storeId = ?2 ")
    Integer getVideoFollowByFollowCustomerIdAndCoverFollowCustomerId
            (@Param("followCustomerId") String followCustomerId, @Param("storeId") Long storeId);
}
