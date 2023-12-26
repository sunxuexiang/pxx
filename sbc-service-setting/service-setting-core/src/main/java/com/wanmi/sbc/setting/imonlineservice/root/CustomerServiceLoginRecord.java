package com.wanmi.sbc.setting.imonlineservice.root;


import lombok.Data;

import javax.persistence.*;

/**
 * <p>IM客服登录信息</p>
 * @author zzg
 * @date 2023-08-24 16:10:28
 */
@Data
@Entity
@Table(name = "customer_service_login_record")
public class CustomerServiceLoginRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "login_id")
    private Long loginId;

    /**
     * 公司ID
     */
    @Column(name = "company_info_id")
    private Long companyInfoId;

    /**
     * 店铺ID
     */
    @Column(name = "store_id")
    private Long storeId;

    /**
     * 登录时间
     */
    @Column(name = "login_time")
    private Long loginTime;

    /**
     * 客服服务账号
     */
    @Column(name = "server_account")
    private String serverAccount;

    /**
     * ip地址
     */
    @Column(name = "ip_addr")
    private String ipAddr;
}
