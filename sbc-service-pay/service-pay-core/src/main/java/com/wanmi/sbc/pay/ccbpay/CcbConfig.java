package com.wanmi.sbc.pay.ccbpay;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/6/28 10:31
 */
@Data
@Component
@ConfigurationProperties(prefix = "ccb.config")
public class CcbConfig {

    /**
     * 发起方渠道编号
     */
    private String ittpartyStmId;

    /**
     * 发起方渠道代码
     */
    private String pyChnlCd;

    /**
     * 市场编码
     */
    private String mktId;

    /**
     * 平台公钥
     */
    private String platPk;

    /**
     * 市场方私钥
     */
    private String privateRsa;

    /**
     * 市场方公钥
     */
    private String publicRsa;

    /**
     * 市场方抽佣商家
     */
    private String mktMrchId;

    /**
     * 建行清算规则
     */
    private Long clearDateRule;

    /**
     * 回调地址序号
     */
    private Integer resultNotifySn;

    /**
     * 微信小程序appid
     */
    private String wxAppid;

    /**
     * 微信小程序secret
     */
    private String wxSecret;

    /**
     * 支付宝小程序 appid
     */
    private String aliAppid;

    /**
     * 支付宝应用私钥
     */
    private String aliPrivateKey;

    /**
     * 支付宝公钥
     */
    private String aliPublicKey;

    /**
     * 鲸币账户
     */
    private String coinMktMrchId;

    /**
     * 运费佣金账户 (配送到店)
     */
    private String freightMktMrchId;

    /**
     * 广告收款账号
     */
    private String adMktMrchId;

    /**
     * 支付状态查询时间（分钟）
     */
    private Long payStatusQueryTime;
}
