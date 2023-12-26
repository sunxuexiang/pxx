package com.wanmi.sbc.setting.viewversionconfig.repository;

import com.wanmi.sbc.setting.viewversionconfig.model.root.ViewVersionConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>IOS基础配置DAO</p>
 * @author zhou.jiang
 * @date 2021-09-15
 */
@Repository
public interface ViewVersionConfigRepository extends JpaRepository<ViewVersionConfig, Long>, JpaSpecificationExecutor<ViewVersionConfig> {


    @Query(value = "select * from view_version_config where system_type = :systemType and state = 1 order by version_no desc limit 1",nativeQuery = true)
    ViewVersionConfig getLastViewVersionConfig(String systemType);



    @Query(value = "select * from view_version_config where system_type = :systemType and state = 1 and version_no >= :currentVersion order by version_no desc",nativeQuery = true)
    List<ViewVersionConfig> findLastVersionBySystemTypeAndCurrentVersion(@Param("systemType") String systemType, @Param("currentVersion") String currentVersion);
}
