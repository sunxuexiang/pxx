package com.wanmi.sbc.setting.onlineservice.model.root;

import lombok.Data;

import javax.persistence.*;

/**
 * <p>在线客服店铺关系</p>
 * @author zhouzhenguo
 * @date 20231005
 */
@Data
@Entity
@Table(name = "customer_service_store")
public class CustomerServiceStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "relation_id")
    private Long relationId;

    /**
     * 店铺ID
     */
    @Column(name = "store_id")
    private Long storeId;

    /**
     * 公司ID
     */
    @Column(name = "company_info_id")
    private Long companyInfoId;

}
