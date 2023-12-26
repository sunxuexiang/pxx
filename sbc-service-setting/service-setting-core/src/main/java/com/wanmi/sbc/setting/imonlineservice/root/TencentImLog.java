package com.wanmi.sbc.setting.imonlineservice.root;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tencent_im_log")
public class TencentImLog {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    /**
     * 用户手机号
     */
    @Column(name = "account")
    private String account;

    /**
     * 签名
     */
    @Column(name = "sign")
    private String sign;

    /**
     * 错误码
     */
    @Column(name = "error_code")
    private String errorCode;

    /**
     * 错误描述
     */
    @Column(name = "error_message")
    private String errorMessage;


}
