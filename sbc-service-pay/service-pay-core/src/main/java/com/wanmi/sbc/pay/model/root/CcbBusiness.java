package com.wanmi.sbc.pay.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.pay.bean.enums.CcbDelFlag;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by IntelliJ IDEA.
 * 建行商家信息
 * @Author : Like
 * @create 2023/6/13 15:42
 */
@Data
@Entity
@Table(name = "ccb_business")
@Accessors(chain = true)
public class CcbBusiness implements Serializable {

    private static final long serialVersionUID = -6190599577201421336L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "business_id")
    private Long businessId;

    /**
     * 发起渠道编号
     */
    @Column(name = "ittparty_stm_id")
    private String ittpartyStmId;

    /**
     * 支付渠道代码
     */
    @Column(name = "py_chnl_cd")
    private String pyChnlCd;

    /**
     * 市场商家编号
     */
    @Column(name = "mkt_mrch_id")
    private String mktMrchId;

    /**
     * 市场商家名称
     */
    @Column(name = "mkt_mrch_nm")
    private String mktMrchNm;

    /**
     * POS编号
     */
    @Column(name = "pos_no")
    private String posNo;

    /**
     * 商家证件类型(01组织机构代码证 02营业执照 03其他 04 统一社会信用证代码)
     */
    @Column(name = "mrch_crdt_tp")
    private String mrchCrdtTp;

    /**
     * 商家证件号码
     */
    @Column(name = "mrch_crdt_no")
    private String mrchCrdtNo;

    /**
     * 商家柜台代码
     */
    @Column(name = "mrch_cnter_cd")
    private String mrchCnterCd;

    /**
     * 证件类型(01 代表居民身份证)
     */
    @Column(name = "crdt_tp")
    private String crdtTp;

    /**
     * 联系人名称
     */
    @Column(name = "ctcpsn_nm")
    private String ctcpsnNm;

    /**
     * 证件号码
     */
    @Column(name = "crdt_no")
    private String crdtNo;

    /**
     * 手机号码
     */
    @Column(name = "mblph_no")
    private String mblphNo;

    /**
     * 商家自定义编号
     */
    @Column(name = "udf_id")
    private String udfId;

    /**
     * 创建时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name = "del_flag")
    @Enumerated
    private CcbDelFlag delFlag;

    /**
     * 银行账号
     */
    @Column(name = "clrg_acc_no")
    private String clrgAccNo;

}
