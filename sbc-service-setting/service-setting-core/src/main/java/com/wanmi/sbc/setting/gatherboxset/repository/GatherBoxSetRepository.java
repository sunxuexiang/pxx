package com.wanmi.sbc.setting.gatherboxset.repository;

import com.wanmi.sbc.setting.gatherboxset.model.root.GatherBoxSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>小程序直播设置DAO</p>
 * @author zwb
 * @date 2020-06-19 16:11:36
 */
@Repository
public interface GatherBoxSetRepository extends JpaRepository<GatherBoxSet, String>,
        JpaSpecificationExecutor<GatherBoxSet> {
}
