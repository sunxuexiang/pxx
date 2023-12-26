package com.wanmi.sbc.setting.imonlineservice.root;


import lombok.Data;

import javax.persistence.*;

/**
 * <p>在线客服快捷回复常用语分组</p>
 * @author zhouzhenguo
 * @date 2023-08-31 16:10:28
 */
@Data
@Entity
@Table(name = "customer_service_common_message_group")
public class CustomerServiceCommonMessageGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long groupId;

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
     * 分组名称
     */
    @Column(name = "group_name")
    private String groupName;

    /**
     * 分组层级
     */
    @Column(name = "group_level")
    private Integer groupLevel;


    /**
     * 上级分组ID，0表示一级分组
     */
    @Column(name = "parent_group_id")
    private Long parentGroupId;

    /**
     * 排序，升序
     */
    @Column(name = "sort_num")
    private Integer sortNum;
}
