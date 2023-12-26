package com.wanmi.sbc.setting.sysswitch;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by yuanlinling on 2017/4/26.
 */
@Repository
public interface SwitchRepository extends JpaRepository<Switch, Long>,
        JpaSpecificationExecutor<Switch> {

    /**
     * 根据id查询开关
     * @param id
     * @return
     */
    Optional<Switch> findById(String id);

    @Query(value = "SELECT status from system_switch where del_flag=0 and switch_code=?1",nativeQuery = true)
    List<Integer> findBySwtichCode(String switchCode);

    @Query(value = "SELECT * from system_switch where del_flag=0 and switch_code=?1",nativeQuery = true)
    List<Switch> findSwitchByCode(String switchCode);

    /**
     * 开关开启关闭
     *
     * @param id
     * @param status
     * @return
     */
    @Modifying
    @Query("update Switch s set s.status = ?2  where s.id = ?1")
    int updateSwitch(String id, Integer status);
}
