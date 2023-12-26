package com.wanmi.sbc.setting.imonlineservice.repository;

import com.wanmi.sbc.setting.imonlineservice.root.CustomerServiceChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>客服服务对话表Dao</p>
 * @author SGY
 * @date 2023-06-05 16:10:28
 */
@Repository
public interface CustomerServiceChatRepository extends JpaRepository<CustomerServiceChat, Long>,
        JpaSpecificationExecutor<CustomerServiceChat> {

    @Query(value = "select * from customer_service_chat where server_account = ?1 and customer_im_account = ?2 order by create_time desc limit 0,1", nativeQuery = true)
    CustomerServiceChat findByServiceAccountAndUserAccount(String fromAccount, String toAccount);

    @Query(value = "select * from customer_service_chat where customer_im_account = ?1 and company_info_id = ?2 order by create_time desc limit 0,1", nativeQuery = true)
    CustomerServiceChat findByCustomerImAccountAndCompanyInfoId(String customerImAccount, Long companyInfoId);

    @Query(value = "select * from customer_service_chat where im_group_id = ?1 order by create_time desc limit 0,1", nativeQuery = true)
    CustomerServiceChat findByImGroupId(String groupId);

    @Query(value = "select * from customer_service_chat where chat_id in (" +
            "select max(chat_id) from customer_service_chat where company_info_id = ?1 and chat_state = ?2 and send_state = ?3 and msg_time < ?4 and timeout_state = ?5 group by im_group_id)", nativeQuery = true)
    List<CustomerServiceChat> getReplyTimeoutChatList(Long companyInfoId, Integer chatState, Integer sendState, Long lastMsgTime, Integer timeoutState);

    @Query(value = "select * from customer_service_chat where chat_state = ?1 and send_state = ?2 and msg_time < ?3 and timeout_state = ?4 and service_state = 0 limit 0,?5", nativeQuery = true)
    List<CustomerServiceChat> getReplyTimeoutChatListByPage(Integer chatState, Integer sendState, Long lastMsgTime, Integer timeoutState, Integer pageSize);

    @Query(value = "select * from customer_service_chat where chat_id in (" +
            "select max(chat_id) from customer_service_chat where company_info_id = ?1 and chat_state = ?2 and send_state = ?3 and msg_time < ?4 group by im_group_id)", nativeQuery = true)
    List<CustomerServiceChat> getReplyTimeoutFinishChatList(Long companyInfoId, int chatState, int sendState, Long lastMsgTime);

    @Query(value = "select * from customer_service_chat where chat_state = ?1 and send_state = ?2 and msg_time < ?3 limit 0,?4", nativeQuery = true)
    List<CustomerServiceChat> getReplyTimeoutFinishChatListByPage(int chatState, int sendState, Long lastMsgTime, Integer pageSize);

    @Query(value = "select * from customer_service_chat where chat_state = ?1 and send_state = ?2 and service_state = ?3 and msg_time < ?4 limit 0,?4", nativeQuery = true)
    List<CustomerServiceChat> getTimeoutFinishChatListByPage(int chatState, int sendState, int serviceState, Long lastMsgTime, Integer pageSize);

    @Query(value = "select server_account as serverAccount, count(1) as quantity from customer_service_chat where company_info_id = ?1 and chat_state = ?2 group by server_account", nativeQuery = true)
    List<Map<String, Object>> countAcceptQuantity(Long companyInfo, int chatState);

    @Query(value = "select server_account as serverAccount, count(1) as quantity from customer_service_chat where company_info_id = ?1 and chat_state = ?2 and service_state = ?3 group by server_account", nativeQuery = true)
    List<Map<String, Object>> countCompanyAcceptQuantity(Long companyInfo, int chatState, int serviceState);

    @Query(value = "select * from customer_service_chat where chat_id in (" +
            "select max(chat_id) from customer_service_chat where company_info_id = ?1 and chat_state = ?2 and send_state = ?3 and msg_time < ?4 and user_timeout_state = ?5 group by im_group_id)", nativeQuery = true)
    List<CustomerServiceChat> getUserTimeoutChatList(Long companyInfoId, Integer chatState, Integer sendState, Long lastMsgTime, Integer userTimeoutState);

    @Query(value = "select *  from customer_service_chat where chat_state = ?1 and send_state = ?2 and msg_time < ?3 and user_timeout_state = ?4 and service_state = 0 limit 0, ?5", nativeQuery = true)
    List<CustomerServiceChat> getUserTimeoutChatListByPage(Integer chatState, Integer sendState, Long lastMsgTime, Integer userTimeoutState, Integer pageSize);

    @Query(value = "select * from customer_service_chat where chat_id in (" +
            "select max(chat_id) from customer_service_chat where im_group_id in (?1) group by im_group_id)", nativeQuery = true)
    List<CustomerServiceChat> findByImGroupIds(List<String> groupList);

    List<CustomerServiceChat> findByCompanyInfoIdAndServerAccountAndChatStateAndServiceState(Long companyInfoId, String serverAccount, int chatState, int serviceState);

    List<CustomerServiceChat> findByCompanyInfoIdAndChatStateAndServiceState(Long companyInfoId, int chatState, int serviceState);

    List<CustomerServiceChat> findByServerAccountAndChatState(String customerServiceAccount, Integer chatState);

    @Query(value = "select * from customer_service_chat where chat_id in (" +
            "select max(chat_id) from customer_service_chat where im_group_id in (?1)  and create_time between ?2 and ?3 group by im_group_id) and chat_state <> 0 order by update_time desc", nativeQuery = true)
    List<CustomerServiceChat> findClosedChatByImGroupIds(List<String> groupIdList, Date startTime, Date endTime);

    @Query(value = "select im_group_id from customer_service_chat where chat_id in (" +
            "select max(chat_id) from customer_service_chat where im_group_id in (?1) group by im_group_id) and chat_state in (0, 4)", nativeQuery = true)
    List<String> findChatingGroupIdList(List<String> groupIdList);

    @Query(value = "select count(1) from customer_service_chat where server_account = ?1 and chat_state = 0", nativeQuery = true)
    Integer countAccountQuantityByServerAccount(String customerServiceAccount);

    List<CustomerServiceChat> findByImGroupIdAndChatState(String imGroupId, int chatState);

    List<CustomerServiceChat> findByCompanyInfoIdAndChatState(Long companyInfoId, int chatState);

    @Query(value = "select customer_im_account from customer_service_chat where company_info_id = ?1 and chat_state = ?2 " +
            "and customer_im_account is not null order by create_time asc", nativeQuery = true)
    List<String> findAllQueueCustomerList(Long companyInfoId, Integer chatState);

    @Query(value = "select distinct company_info_id from customer_service_chat where chat_state = 4", nativeQuery = true)
    List<Long> findAllQueueCompanyId();

    @Query(value = "select * from customer_service_chat where customer_im_account = ?1 and source_company_id = ?2 order by create_time desc limit 0,1", nativeQuery = true)
    CustomerServiceChat findByCustomerImAccountAndSourceCompanyId(String customerImAccount, Long companyInfoId);

    List<CustomerServiceChat> findByCompanyInfoIdAndChatStateOrderByChatIdAsc(Long companyInfoId, Integer chatState);

    @Query(value = "select chat_id from customer_service_chat where company_info_id = ?1 and chat_state = ?2 order by chat_id asc", nativeQuery = true)
    List<Long> findChatIdByCompanyInfoIdAndChatStateOrderByChatIdAsc(Long companyInfoId, Integer chatState);

    @Query(value = "select * from customer_service_chat where chat_id in (" +
            "select max(chat_id) from customer_service_chat where im_group_id in (?1) and server_account = ?2 and create_time between ?3 and ?4 group by im_group_id) and chat_state <> 0 order by update_time desc", nativeQuery = true)
    List<CustomerServiceChat> findClosedChatByImGroupIdsAndServerAccount(List<String> searchGroupIdList, String serverAccount, Date startTime, Date endTime);

    @Query(value = "select * from customer_service_chat where chat_state = ?1 and service_state = ?2 and send_leave = ?3 and msg_time < ?4 limit 0,?5", nativeQuery = true)
    List<CustomerServiceChat> findByTimeoutOffliceChat(int chatState, int serviceState, int sendLeave, Long timeoutTime, int pageSize);

    @Query(value = "select distinct(im_group_id) from customer_service_chat where company_info_id = ?1", nativeQuery = true)
    List<String> findGroupIdListByCompanyId(Long companyInfoId);

    @Query(value = "SELECT * from customer_service_chat where chat_id in (select max(chat_id) FROM `customer_service_chat` group by im_group_id)", nativeQuery = true)
    List<CustomerServiceChat> findAllGroupChat();
}
