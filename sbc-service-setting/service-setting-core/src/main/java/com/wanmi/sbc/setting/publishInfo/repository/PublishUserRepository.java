package com.wanmi.sbc.setting.publishInfo.repository;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.setting.publishInfo.root.PublishUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * <p>信息发布用户</p>
 * @author lwp
 * @date 2023/10/18
 */
@Repository
public interface PublishUserRepository extends JpaRepository<PublishUser, Long>,
        JpaSpecificationExecutor<PublishUser> {

    PublishUser findByUserNameAndUserPassAndDelFlag(String userName, String userPass, DeleteFlag deleteFlag);


}
