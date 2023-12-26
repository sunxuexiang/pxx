package com.wanmi.sbc.setting.activityconfig.repository;

import com.wanmi.sbc.setting.activityconfig.model.root.ActivityConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.wanmi.sbc.common.enums.DeleteFlag;
import java.util.Optional;
import java.util.List;

/**
 * <p>导航配置DAO</p>
 * @author lvheng
 * @date 2021-04-19 18:49:30
 */
@Repository
public interface ActivityConfigRepository extends JpaRepository<ActivityConfig, Long>,
        JpaSpecificationExecutor<ActivityConfig> {

    /**
     * 单个删除导航配置
     * @author lvheng
     */
    @Modifying
    @Query("update ActivityConfig set delFlag = 1 where id = ?1")
    void deleteById(Long id);

    /**
     * 批量删除导航配置
     * @author lvheng
     */
    @Modifying
    @Query("update ActivityConfig set delFlag = 1 where id in ?1")
    void deleteByIdList(List<Long> idList);

    Optional<ActivityConfig> findByIdAndDelFlag(Long id, DeleteFlag delFlag);

}
