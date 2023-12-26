package com.wanmi.sbc.setting.advertising.repository;

import com.wanmi.sbc.setting.advertising.model.root.AdvertisingRetailGoodsConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description: 散批分栏商品配置信息
 * @Author: XinJiang
 * @Date: 2022/4/20 16:21
 */
@Repository
public interface AdvertisingRetailGoodsConfigRepository extends JpaRepository<AdvertisingRetailGoodsConfig,String>, JpaSpecificationExecutor<AdvertisingRetailGoodsConfig> {

    /**
     * 通过配置表id删除关联商品信息
     * @param
     * @return
     */
    @Query("delete from AdvertisingRetailGoodsConfig where advertisingConfigId in ?1")
    @Modifying
    int deleteByConfigIds(List<String> configIds);
}
