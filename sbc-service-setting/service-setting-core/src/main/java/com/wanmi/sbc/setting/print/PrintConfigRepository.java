package com.wanmi.sbc.setting.print;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 系统配置数据源
 * Created by daiyitian on 2017/04/11.
 */
@Repository
public interface PrintConfigRepository extends JpaRepository<PrintConfig, Long>, JpaSpecificationExecutor<PrintConfig> {

}
