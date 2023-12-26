package com.wanmi.sbc.setting.hotstylemoments.repository;

import com.wanmi.sbc.setting.hotstylemoments.model.root.HotStyleMomentsConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @Description: 爆款时刻商品配置信息持久层
 * @Author: XinJiang
 * @Date: 2022/5/9 18:42
 */
@Repository
public interface HotStyleMomentsConfigRepository extends JpaRepository<HotStyleMomentsConfig, Long>, JpaSpecificationExecutor<HotStyleMomentsConfig> {

    /**
     * 通过爆款活动id删除商品配置信息
     * @param hotId
     * @return
     */
    int deleteByHotId(Long hotId);
}
