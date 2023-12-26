package com.wanmi.sbc.setting.publishInfo.repository;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.setting.config.Config;
import com.wanmi.sbc.setting.publishInfo.root.PublishInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>信息发布</p>
 * @author lwp
 * @date 2023/10/18
 */
@Repository
public interface PublishInfoRepository extends JpaRepository<PublishInfo, Long>,
        JpaSpecificationExecutor<PublishInfo> {

    PublishInfo findByTitleAndDelFlag(String title, DeleteFlag delFlag);


}
