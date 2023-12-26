package com.wanmi.sbc.setting.advertising.repository;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.setting.advertising.model.root.StartPageAdvertising;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @Description: 启动页广告配置持久层
 * @Author: XinJiang
 * @Date: 2022/3/31 10:58
 */
@Repository
public interface StartPageAdvertisingRepository extends JpaRepository<StartPageAdvertising,String>,
        JpaSpecificationExecutor<StartPageAdvertising> {

    /**
     * 修改所有未删除的启动页广告信息为关闭状态
     * @return
     */
    @Modifying
    @Query("update StartPageAdvertising set status = 0 where delFlag = 0")
    int modifyAllStartPageStatus();

    /**
     * 根据主键id修改启动页广告信息状态
     * @param advertisingId
     * @param status
     * @return
     */
    @Modifying
    @Query("update StartPageAdvertising set status = ?2 where advertisingId = ?1")
    int modifyStartPageStatusById(String advertisingId, DefaultFlag status);
}
