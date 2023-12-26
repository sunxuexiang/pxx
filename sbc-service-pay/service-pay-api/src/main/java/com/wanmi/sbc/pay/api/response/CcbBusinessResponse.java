package com.wanmi.sbc.pay.api.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.pay.bean.enums.CcbDelFlag;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/10/27 16:36
 */
@Data
public class CcbBusinessResponse implements Serializable {
    private static final long serialVersionUID = 7897910756231711638L;

    private Long businessId;

    /**
     * 发起渠道编号
     */
    private String ittpartyStmId;

    /**
     * 支付渠道代码
     */
    private String pyChnlCd;

    /**
     * 市场商家编号
     */
    private String mktMrchId;

    /**
     * 市场商家名称
     */
    private String mktMrchNm;

    /**
     * POS编号
     */
    private String posNo;

    /**
     * 商家证件类型(01组织机构代码证 02营业执照 03其他 04 统一社会信用证代码)
     */
    private String mrchCrdtTp;

    /**
     * 商家证件号码
     */
    private String mrchCrdtNo;

    /**
     * 商家柜台代码
     */
    private String mrchCnterCd;

    /**
     * 证件类型(01 代表居民身份证)
     */
    private String crdtTp;

    /**
     * 联系人名称
     */
    private String ctcpsnNm;

    /**
     * 证件号码
     */
    private String crdtNo;

    /**
     * 手机号码
     */
    private String mblphNo;

    /**
     * 商家自定义编号
     */
    private String udfId;

    /**
     * 创建时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

    private CcbDelFlag delFlag;

    /**
     * 银行账号
     */
    private String clrgAccNo;
}
