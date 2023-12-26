package com.wanmi.sbc.setting.weatherswitch.repository;

import com.wanmi.sbc.setting.weatherswitch.model.root.WeatherSwitch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * <p>天气设置DAO</p>
 * @author 费传奇
 * @date 2021-04-16 09:54:37
 */
@Repository
public interface WeatherSwitchRepository extends JpaRepository<WeatherSwitch, Long>,
        JpaSpecificationExecutor<WeatherSwitch> {

}
