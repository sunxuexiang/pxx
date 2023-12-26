package com.wanmi.sbc.setting.imonlineservice.repository;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.setting.imonlineservice.root.ImOnlineService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * <p>ImOnlineServiceRepositoryDao</p>
 * @author SGY
 * @date 2023-06-05 16:10:28
 */
@Repository
public interface ImOnlineServiceRepository extends JpaRepository<ImOnlineService, Integer>,
        JpaSpecificationExecutor<ImOnlineService> {

    /**
     * 通过店铺id appKey 查询在线客服
     * @param storeId
     * @param deleteFlag
     * @return
     */
    ImOnlineService findByCompanyInfoIdAndDelFlag(Long companyInfoId, DeleteFlag deleteFlag);

}
