package com.wanmi.sbc.setting.logisticscompany.repository;

import com.wanmi.sbc.setting.logisticscompany.model.root.LogisticsCompanyLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @desc  物流线路
 * @author shiy  2023/11/7 9:12
*/
@Repository
public interface LogisticsCompanyLineRepository extends JpaRepository<LogisticsCompanyLine, Long>,
        JpaSpecificationExecutor<LogisticsCompanyLine> {

}
