package com.wanmi.sbc.setting.systemstoreconfig.repository;

import com.wanmi.sbc.setting.systemstoreconfig.model.root.SystemStoreConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>系统配置表DAO</p>
 * @author yang
 * @date 2019-11-05 18:33:04
 */
@Repository
public interface SystemStoreConfigRepository extends JpaRepository<SystemStoreConfig, Long>,
        JpaSpecificationExecutor<SystemStoreConfig> {

    /**
     * 单个删除系统配置表
     * @author yang
     */
    @Modifying
    @Query("update SystemStoreConfig set delFlag = 1 where id = ?1")
    void deleteById(Long id);

    /**
     * 批量删除系统配置表
     * @author yang
     */
    @Modifying
    @Query("update SystemStoreConfig set delFlag = 1 where id in ?1")
    int deleteByIdList(List<Long> idList);

}
