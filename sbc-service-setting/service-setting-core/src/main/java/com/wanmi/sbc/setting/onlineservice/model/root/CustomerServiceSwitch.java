package com.wanmi.sbc.setting.onlineservice.model.root;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * <p>客服开关实体类</p>
 * @author zhouzhenguo
 * @date 20230729
 */
@Data
@Entity
@Table(name = "customer_service_switch")
public class CustomerServiceSwitch implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 公司ID
     */
    @Column(name = "company_info_id")
    private Long companyInfoId;

    /**
     * 在线客服启用类型：0、全部未启用；1、腾讯IM；2、智齿
     */
    @Column(name = "service_switch_type")
    private Integer serviceSwitchType;
}
