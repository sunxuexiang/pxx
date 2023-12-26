package com.wanmi.sbc.message.imhistory.repository;

import com.wanmi.sbc.message.imhistory.root.ImHistoryMsg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * <p>im历史</p>
 * @author sgy
 * @date 2023-07-03 15:49:24
 */
@Repository
public interface ImHistoryMsgRepository extends JpaRepository<ImHistoryMsg, Long>,
        JpaSpecificationExecutor<ImHistoryMsg> {


}
