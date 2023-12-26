package com.wanmi.sbc.setting.hotstylemoments.repository;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.setting.hotstylemoments.model.root.HotStyleMoments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.validation.groups.Default;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description: 爆款时刻持久层接口
 * @Author: XinJiang
 * @Date: 2022/5/9 18:41
 */
@Repository
public interface HotStyleMomentsRepository extends JpaRepository<HotStyleMoments, Long>, JpaSpecificationExecutor<HotStyleMoments> {

    /**
     * 终止爆款时刻活动
     * @param hotId
     * @return
     */
    @Modifying
    @Query("update HotStyleMoments set terminationFlag = 1 where hotId = ?1")
    int terminationHotStyleMoments(Long hotId);

    /**
     * 暂停或启动爆款时刻活动
     * @param pauseFlag
     * @param hotId
     * @return
     */
    @Modifying
    @Query("update HotStyleMoments set isPause = ?1 where hotId = ?2")
    int pauseHotStyleMoments(DefaultFlag pauseFlag, Long hotId);

    /**
     * 提前开始活动
     * @param beginTime
     * @param hotId
     * @return
     */
    @Modifying
    @Query("update HotStyleMoments set beginTime = ?1 where hotId = ?2")
    int earlyStart(LocalDateTime beginTime, Long hotId);

    /**
     * 逻辑删除
     * @param deletePerson
     * @param deleteTime
     * @param hotId
     * @return
     */
    @Modifying
    @Query("update HotStyleMoments set delFlag = 1,deletePerson = ?1,deleteTime = ?2 where hotId = ?3")
    int delById(String deletePerson, LocalDateTime deleteTime, Long hotId);

    /**
     * 获取重复时间内的爆款时刻
     * @param beginTime
     * @return
     */
    @Query("from HotStyleMoments where beginTime <= ?1 and endTime >= ?1 and delFlag = 0 and isPause = 0 and terminationFlag = 0")
    List<HotStyleMoments> checkTime(LocalDateTime beginTime);

    /**
     * 获取重复时间内的爆款时刻 排除自身（修改时）
     * @param beginTime
     * @param hotId
     * @return
     */
    @Query("from HotStyleMoments where beginTime <= ?1 and endTime >= ?1 and delFlag = 0 and isPause = 0 and terminationFlag = 0 and hotId <> ?2")
    List<HotStyleMoments> checkTimeNotSelf(LocalDateTime beginTime,Long hotId);

}
