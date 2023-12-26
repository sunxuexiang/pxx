package com.wanmi.sbc.setting.advertising.repository;

import com.wanmi.sbc.setting.advertising.model.root.Advertising;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @Description: 首页广告位持久层接口
 * @Author: XinJiang
 * @Date: 2022/2/18 9:56
 */
@Repository
public interface AdvertisingRepository extends JpaRepository<Advertising,String>, JpaSpecificationExecutor<Advertising> {

}
