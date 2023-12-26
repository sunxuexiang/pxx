package com.wanmi.sbc.setting.imonlineservice.repository;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.setting.imonlineservice.root.CommonChatMsg;
import com.wanmi.sbc.setting.imonlineservice.root.ImOnlineService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>ImOnlineServiceRepositoryDao</p>
 * @author SGY
 * @date 2023-06-05 16:10:28
 */
@Repository
public interface CommonChatMsgRepository extends JpaRepository<CommonChatMsg, Long>,
        JpaSpecificationExecutor<CommonChatMsg> {


    Integer deleteByImOnlineServiceId(Long imOnlineServiceId);

    List<CommonChatMsg> findByImOnlineServiceId(Long imOnlineServiceId);

    List<CommonChatMsg> findByImOnlineServiceIdOrderBySortNumDesc(Long imOnlineServiceId);

    List<CommonChatMsg> findByImOnlineServiceIdOrderBySortNumAsc(Long imOnlineServiceId);
}
