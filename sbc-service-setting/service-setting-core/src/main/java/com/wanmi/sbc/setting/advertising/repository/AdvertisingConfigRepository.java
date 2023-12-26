package com.wanmi.sbc.setting.advertising.repository;

import com.wanmi.sbc.setting.advertising.model.root.AdvertisingConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description: 首页广告位配置持久层接口
 * @Author: XinJiang
 * @Date: 2022/2/18 9:59
 */
@Repository
public interface AdvertisingConfigRepository extends JpaRepository<AdvertisingConfig,String>, JpaSpecificationExecutor<AdvertisingConfig> {

    /**
     * 通过首页广告位id删除配置信息
     * @param advertisingId
     * @return
     */
    int deleteByAdvertisingId(String advertisingId);

    /**
     * 通过首页广告位id查询配置信息
     * @param advertisingId
     * @return
     */
    List<AdvertisingConfig> getAdvertisingConfigByAdvertisingId(String advertisingId);
}
