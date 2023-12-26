package com.wanmi.sbc.customer.log.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description  用户日志表
 * @author  shiy
 * @date    2023/4/7 10:56
 * @params
 * @return
*/
@Data
@Entity
@Table(name = "customer_log")
public class CustomerLog implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 会员标识UUID
     */
    @Column(name = "customer_id")
    private String customerId;

    /**
     * 账号
     */
    @Column(name = "user_no")
    private String userNo;


    /**
     * 1APP登录
     */
    @Column(name = "log_type")
    private Integer logType;

    /**
     * 登录IP
     */
    @Column(name = "user_ip")
    private String userIp;

    /**
     * 1android,2ios
     */
    @Column(name = "app_type")
    private Integer appType;

    /**
     * app版本
     */
    @Column(name = "app_version")
    private String appVersion;

    /**
     * 设备信息
     */
    @Column(name = "dev_info")
    private String devInfo;

    /**
     * mac地址
     */
    @Column(name = "mac_addr")
    private String macAddr;

    /**
     * 创建时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "create_time")
    private LocalDateTime createTime;
}