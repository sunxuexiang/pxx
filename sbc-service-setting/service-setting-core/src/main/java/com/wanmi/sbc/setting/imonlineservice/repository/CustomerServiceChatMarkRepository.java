package com.wanmi.sbc.setting.imonlineservice.repository;

import com.wanmi.sbc.setting.imonlineservice.root.CustomerServiceChatMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CustomerServiceChatMarkRepository extends JpaRepository<CustomerServiceChatMark, Long>,
        JpaSpecificationExecutor<CustomerServiceChatMark> {
    List<CustomerServiceChatMark> findByMarkDateAndServerAccountAndImGroupId(int today, String serverAccount, String imGroupId);

    @Modifying
    @Transactional
    @Query(value = "delete from customer_service_chat_mark where mark_date = ?1 and server_account = ?2 and im_group_id = ?3", nativeQuery = true)
    void deleteByMarkDateAndServerAccountAndImGroupId(int today, String serverAccount, String imGroupId);

    List<CustomerServiceChatMark> findByMarkDateAndServerAccount(int today, String fromAccount);
}
