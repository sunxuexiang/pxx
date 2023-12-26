package com.wanmi.sbc.setting.imonlineservice.root;

import lombok.Data;

import javax.persistence.*;

/**
 * <p>客服快捷回复常用语表</p>
 * @author zhouzhenguo
 * @date 2023-08-31 16:10:28
 */
@Data
@Entity
@Table(name = "customer_service_setting")
public class CustomerServiceSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "setting_id")
    private Long settingId;

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
     * 消息内容
     */
    @Column(name = "content_json")
    private String contentJson;

    /**
     * 设置类型：1、人工客服欢迎语；2、客服超时说辞；3、客户超时下线提示；4、接收离线消息账号；5、客户超时说辞；6、客服不在线说辞
     */
    @Column(name = "setting_type")
    private Integer settingType;

    /**
     * 操作用户ID
     */
    @Column(name = "operator_id")
    private String operatorId;

}
