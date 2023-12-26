package com.wanmi.sbc.setting.iosappversionconfig.repository;

import com.wanmi.sbc.setting.api.request.iosappversionconfig.IosAppVersionConfigAddRequest;
import com.wanmi.sbc.setting.iosappversionconfig.model.root.IosAppVersionConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>IOS基础配置DAO</p>
 * @author zhou.jiang
 * @date 2021-09-15
 */
@Repository
public interface IosAppVersionConfigRepository extends JpaRepository<IosAppVersionConfig, Long>,
        JpaSpecificationExecutor<IosAppVersionConfig> {
    List<IosAppVersionConfig> findByVersionNoOrderByBuildNoDesc(String versionNo);

    /**
     * 获取最新配置信息
     * @return
     */
    @Query(value = "from ios_app_version_config order by last_version_update_time desc limit 1",nativeQuery = true)
    List<IosAppVersionConfig> getIosAppVersionConfig();

    /**
     * 获取最新版本号
     * @return
     */
    @Query(value = "select version_no from ios_app_version_config order by build_no desc limit 1",nativeQuery = true)
    String getLastVersionNo();

    /**
     * 根据版本号和构建号获取配置信息
     * @param versionNo
     * @param BuildNo
     * @return
     */
    IosAppVersionConfig getIosAppVersionConfigByVersionNoAndBuildNo(String versionNo, Long BuildNo);

    /**
     * 根据主键id获取版本配置信息
     * @param id
     * @return
     */
    IosAppVersionConfig getById(Long id);
}
