package com.wanmi.sbc.live.hostAccount.dao;

import com.wanmi.sbc.live.api.request.host.LiveHostInfoRequest;
import com.wanmi.sbc.live.hostAccount.model.root.LiveHostAccount;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>主播账户Mapper</p>
 * @author 王冬明（1010331559@qq.com） Automatic Generator
 * @date 2022-09-19 12:46:26
 * @version 1.0
 * @package com.wanmi.sbc.live.host_account.dao
 */
@Repository
public interface LiveHostAccountMapper {

    /**
     * 新增
     * @param request
     * @return
     */
    int add(LiveHostAccount request);

    /**
     * 修改
     * @param request
     * @return
     */
    int modify(LiveHostAccount request);

//    /**
//     * 根据直播账户ID查询
//     * @param customerId
//     * @return
//     */
//    LiveHostAccount getInfoByCustomerId(String customerId);

    List<LiveHostAccount> getListByHostId(Integer hostId);

    int getEnableCountByHostId(Integer hostId);

    /**
     * 获取一个可用的关联直播账户
     * @param request
     * @return
     */
    LiveHostAccount getInfo(@Param("para") LiveHostInfoRequest request);

    List<String> getEnableCustomerAccountList();
}