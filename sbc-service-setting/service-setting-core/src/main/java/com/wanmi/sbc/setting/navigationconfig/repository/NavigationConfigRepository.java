package com.wanmi.sbc.setting.navigationconfig.repository;

import com.wanmi.sbc.setting.navigationconfig.model.root.NavigationConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * <p>导航配置DAO</p>
 * @author lvheng
 * @date 2021-04-12 14:46:18
 */
@Repository
public interface NavigationConfigRepository extends JpaRepository<NavigationConfig, Integer>,
        JpaSpecificationExecutor<NavigationConfig> {

}
