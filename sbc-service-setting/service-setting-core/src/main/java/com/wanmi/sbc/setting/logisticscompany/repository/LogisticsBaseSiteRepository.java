package com.wanmi.sbc.setting.logisticscompany.repository;

import com.wanmi.sbc.setting.logisticscompany.model.root.LogisticsBaseSite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @desc  物流线路
 * @author shiy  2023/11/7 9:12
*/
@Repository
public interface LogisticsBaseSiteRepository extends JpaRepository<LogisticsBaseSite, Long>,
        JpaSpecificationExecutor<LogisticsBaseSite> {

    @Query(value = "select site_name from logistics_base_site t where t.site_crt_type=0 and t.del_flag=0 and t.create_person=?1 order by t.site_id desc limit 1",nativeQuery = true)
    String getLatestSiteNameByCustomerId(String customerId);

    @Query(value = "select site_name from logistics_base_site t where t.site_crt_type=0 and t.del_flag=0 and t.logistics_id=?1 order by t.site_id desc limit 1",nativeQuery = true)
    String getLatestSiteNameByLogisticsId(String logisticsId);
}
