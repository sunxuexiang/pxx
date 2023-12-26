package com.wanmi.sbc.setting.invitation;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.setting.config.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 邀新配置数据源
 * Created by daiyitian on 2017/04/11.
 */
@Repository
public interface InvitationConfigRepository extends JpaRepository<InvitationConfig, Long>, JpaSpecificationExecutor<InvitationConfig> {


}
