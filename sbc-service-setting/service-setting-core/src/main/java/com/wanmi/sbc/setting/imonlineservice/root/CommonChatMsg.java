package com.wanmi.sbc.setting.imonlineservice.root;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * <p>IM客服常用语</p>
 * @author zzg
 * @date 2023-08-24 16:10:28
 */
@Data
@Entity
@Table(name = "common_chat_message")
public class CommonChatMsg implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "msg_id")
    private Long id;

    /**
     * 在线客服主键
     */
    @Column(name = "im_online_service_id")
    private Long imOnlineServiceId;

    /**
     * 消息内容
     */
    @Column(name = "message")
    private String message;

    /**
     * 排序，升序
     */
    @Column(name = "sort_num")
    private Integer sortNum;

    /**
     * 类型：0、智能回复；1、快捷回复
     */
    @Column(name = "msg_type")
    private Integer msgType;

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
}
