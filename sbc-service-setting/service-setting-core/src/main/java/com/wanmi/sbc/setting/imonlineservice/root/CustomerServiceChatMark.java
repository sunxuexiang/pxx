package com.wanmi.sbc.setting.imonlineservice.root;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "customer_service_chat_mark")
public class CustomerServiceChatMark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mark_id")
    private Long markId;

    /**
     * 客服服务账号
     */
    @Column(name = "server_account")
    private String serverAccount;

    /**
     * 腾讯IM群组ID
     */
    @Column(name = "im_group_id")
    private String imGroupId;

    /**
     * 标记日期
     */
    @Column(name = "mark_date")
    private Integer markDate;
}
