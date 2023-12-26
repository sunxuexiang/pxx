package com.wanmi.sbc.customer.model.root;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.ms.util.jpa.LocalDateTimeConverter;
import com.wanmi.sbc.common.enums.CustomerRegisterType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.customer.bean.enums.CheckState;
import com.wanmi.sbc.customer.bean.enums.CustomerTag;
import com.wanmi.sbc.customer.bean.enums.CustomerType;
import com.wanmi.sbc.customer.bean.enums.EnterpriseCheckState;
import com.wanmi.sbc.customer.detail.model.root.CustomerDetail;
import com.wanmi.sbc.customer.storecustomer.root.StoreCustomerRela;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 客户信息主表
 * Created by CHENLI on 2017/4/13.
 */
@Entity
@Table(name = "customer")
@Data
@EqualsAndHashCode(exclude = "customerDetail")
public class Customer implements Serializable {

    private static final long serialVersionUID = 8433358808181085141L;
    /**
     * 客户ID
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "customer_id")
    private String customerId;

    /**
     * 客户等级ID
     */
    @Column(name = "customer_level_id")
    private Long customerLevelId;

    /**
     * 客户成长值
     */
    @Column(name = "growth_value")
    private Long growthValue;

    /**
     * 可用积分
     */
    @Column(name = "points_available")
    private Long pointsAvailable;

    /**
     * 已用积分
     */
    @Column(name = "points_used")
    private Long pointsUsed;

    /**
     * 账户
     */
    @Column(name = "customer_account")
    private String customerAccount;

    /**
     * 密码
     */
    @Column(name = "customer_password")
    private String customerPassword;


    /**
     * 密码安全等级：20危险 40低、60中、80高
     */
    @Column(name = "safe_level")
    private Integer safeLevel;

    /**
     * 盐值，用于密码加密
     */
    @Column(name = "customer_salt_val")
    private String customerSaltVal;

    /**
     * 审核状态 0：待审核 1：已审核 2：审核未通过
     */
    @Column(name = "check_state")
    @Enumerated
    private CheckState checkState;

    /**
     * 审核时间
     */
    @Column(name = "check_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime checkTime;

    /**
     * 删除标志 0未删除 1已删除
     */
    @Column(name = "del_flag")
    @Enumerated
    private DeleteFlag delFlag;

    /**
     * 登录IP
     */
    @Column(name = "login_ip")
    private String loginIp;

    /**
     * 支付密码
     */
    @Column(name = "customer_pay_password")
    private String customerPayPassword;

    /**
     * 支付密码错误次数
     */
    @Column(name = "pay_error_time")
    private Integer payErrorTime = 0;

    /**
     * 支付锁定时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "pay_lock_time")
    private LocalDateTime payLockTime;

    /**
     * 登录时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "login_time")
    private LocalDateTime loginTime;

    /**
     * 密码错误次数
     */
    @Column(name = "login_error_time")
    private Integer loginErrorCount = 0;

    /**
     * 创建|注册时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    @Column(name = "create_person")
    private String createPerson;

    /**
     * 修改时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 修改人
     */
    @Column(name = "update_person")
    private String updatePerson;

    /**
     * 删除时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "delete_time")
    private LocalDateTime deleteTime;

    /**
     * 删除人
     */
    @Column(name = "delete_person")
    private String deletePerson;

    /**
     * 会员的详细信息
     */
    @OneToOne(mappedBy = "customer")
    @JsonManagedReference
    private CustomerDetail customerDetail;

    /**
     * 客户类型（0:平台客户,1:商家客户）
     */
    @Column(name = "customer_type")
    private CustomerType customerType;

    /**
     * 注册类型（0:普通客户,1:商户,2:单位）
     */
    @Column(name = "customer_register_type")
    private CustomerRegisterType customerRegisterType;


    /**
     * 会员标签（0:零食店，1:便利店，2:商超，3:二批商，4:水果零售店，5:连锁系统，6:炒货店）
     */
    @Column(name = "customer_tag")
    private CustomerTag customerTag;

    /**
     * 锁定时间
     */
    @Column(name = "login_lock_time", insertable = false)
    @Convert(converter = LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime loginLockTime;

    /**
     * 商家和客户的关联关系
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private List<StoreCustomerRela> storeCustomerRelaListByAll;

    /**
     * 连续签到天数
     */
    @Column(name = "sign_continuous_days")
    private Integer signContinuousDays;

    /**
     * 企业购会员审核状态  0：无状态 1：待审核 2：已审核 3：审核未通过
     */
    @Column(name = "enterprise_check_state")
    @Enumerated
    private EnterpriseCheckState enterpriseCheckState;

    /**
     * 企业购会员审核拒绝原因
     */
    @Column(name = "enterprise_check_reason")
    private String enterpriseCheckReason;


    /**
     * 企业名称
     */
    @Column(name = "enterprise_name")
    private String enterpriseName;

    /**
     * 统一社会信用代码
     */
    @Column(name = "social_credit_code")
    private String socialCreditCode;

    /**
     * 营业执照地址
     */
    @Column(name = "business_license_url")
    private String businessLicenseUrl;

    /**
     * 喜吖吖 企业购会员审核状态  0：无状态 1：待审核 2：已审核 3：审核未通过
     */
    @Column(name = "enterprise_status_xyy")
    @Enumerated
    private EnterpriseCheckState enterpriseStatusXyy;

    /**
     * 父Id
     */
    @Column(name = "parent_customer_id")
    private String parentCustomerId;

    /**
     * 会员的erpId
     */
    @Column(name = "customer_erp_id")
    private String customerErpId;

    /**
     * erp的同步标志位
     */
    @Column(name = "erp_async_flag")
    private DefaultFlag erpAsyncFlag;

    /**
     * 大客户标识 0否，1是
     */
    @Column(name = "vip_flag")
    @Enumerated
    private DefaultFlag vipFlag = DefaultFlag.NO;

    /**
     * 用户来源渠道
     */
    @Column(name = "channel")
    private String channel;

    /**
     * 邀请账号
     */
    @Column(name = "invitee_account")
    private String inviteeAccount;
}
