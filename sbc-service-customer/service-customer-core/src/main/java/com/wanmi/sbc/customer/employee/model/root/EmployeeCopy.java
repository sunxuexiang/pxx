package com.wanmi.sbc.customer.employee.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateSerializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.customer.bean.enums.AccountState;
import com.wanmi.sbc.customer.bean.enums.GenderType;
import com.wanmi.sbc.customer.detail.model.root.CustomerDetail;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author lm
 * @date 2022/09/15 8:40
 */
@Entity
@Data
@Table(name = "employee_copy")
public class EmployeeCopy {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "employee_id")
    private String employeeId;

    /**
     * 会员名称
     */
    @Column(name = "employee_name")
    private String employeeName;

    /**
     * 会员电话
     */
    @Column(name = "employee_mobile")
    private String employeeMobile;

    /**
     * 权限标识
     */
    @Column(name = "perm_type")
    private Integer permType;


    /**
     * 账户名
     */
    @Column(name = "account_name")
    private String accountName;

    /**
     * 密码
     */
    @Column(name = "account_password")
    private String accountPassword;

    /**
     * salt
     */
    @Column(name = "employee_salt_val")
    private String employeeSaltVal;

    /**
     * 账号状态
     */
    @Column(name = "account_state")
    @Enumerated
    private AccountState accountState;

    /*管理区域*/
    @Column(name = "manage_area")
    private String manageArea;


    /**
     * 删除标志
     */
    @Column(name = "del_flag")
    @Enumerated
    private DeleteFlag delFlag = DeleteFlag.NO;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;


    /**
     * 登录失败次数
     */
    @Column(name = "login_error_time", insertable = false)
    private Integer loginErrorTime = 0;

    /**
     * 锁定时间
     */
    @Column(name = "login_lock_time", insertable = false)
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime loginLockTime;

    /**
     * 会有登录时间
     */
    @Column(name = "login_time", insertable = false)
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime loginTime;

    /**
     * 邮箱
     */
    @Column(name = "email")
    private String email;

    /**
     * 工号
     */
    @Column(name = "job_no")
    private String jobNo;

    /**
     * 职位
     */
    @Column(name = "position")
    private String position;

    /**
     * 生日
     */
    @Column(name = "birthday")
    @Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate birthday;

    /**
     * 性别，0：保密，1：男，2：女
     */
    @Column(name = "sex")
    private GenderType sex;

    @Column(name = "province")
    private String province;
    @Column(name = "city")
    private String city;
    @Column(name = "area")
    private String area;
}
