package com.wanmi.sbc.setting.advertising.repository;

import com.wanmi.sbc.setting.advertising.model.root.AdvertisingRetailConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description: 散批广告位配置信息持久层接口
 * @Author: XinJiang
 * @Date: 2022/4/18 17:24
 */
@Repository
public interface AdvertisingRetailConfigRepository extends JpaRepository<AdvertisingRetailConfig,String>, JpaSpecificationExecutor<AdvertisingRetailConfig> {

    /**
     * 通过散批广告位id删除配置信息
     * @param advertisingId
     * @return
     */
    int deleteByAdvertisingId(String advertisingId);

    /**
     * 通过散批广告位id获取配置信息
     * @param advertisingId
     * @return
     */
    List<AdvertisingRetailConfig> findAdvertisingRetailConfigByAdvertisingId(String advertisingId);
}
