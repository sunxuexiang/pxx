package com.wanmi.sbc.setting.advertising.repository;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.setting.advertising.model.root.AdvertisingRetail;
import com.wanmi.sbc.setting.bean.enums.AdvertisingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @Description: 散批广告位持久层接口
 * @Author: XinJiang
 * @Date: 2022/4/18 17:23
 */
@Repository
public interface AdvertisingRetailRepository extends JpaRepository<AdvertisingRetail,String>, JpaSpecificationExecutor<AdvertisingRetail> {

    /**
     * 修改所有未删除的启动页广告信息为关闭状态
     * @return
     */
    @Modifying
    @Query("update AdvertisingRetail set status = 0 where delFlag = 0 and advertisingType = ?1")
    int modifyAllStatusByType(AdvertisingType type);

    /**
     * 根据主键id修改启动页广告信息状态
     * @param advertisingId
     * @param status
     * @return
     */
    @Modifying
    @Query("update AdvertisingRetail set status = ?2 where advertisingId = ?1")
    int modifyStatusById(String advertisingId, DefaultFlag status);

}
