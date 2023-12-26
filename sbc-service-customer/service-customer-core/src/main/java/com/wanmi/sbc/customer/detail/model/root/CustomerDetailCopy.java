package com.wanmi.sbc.customer.detail.model.root;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.annotation.CanEmpty;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.GenderType;
import com.wanmi.sbc.common.util.CustomLocalDateDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateSerializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.customer.bean.enums.CustomerStatus;
import com.wanmi.sbc.customer.model.root.Customer;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author lm
 * @date 2022/09/19 19:40
 */
@Entity
@Table(name = "customer_detail_copy")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDetailCopy implements Serializable {



    private static final long serialVersionUID = -1844935220339224079L;
    /**
     * 会员详细信息标识UUID
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "customer_detail_id")
    private String customerDetailId;

    /**
     * 会员ID
     */
    @Column(name = "customer_id")
    private String customerId;

    /**
     * 会员名称
     */
    @Column(name = "customer_name")
    private String customerName;

    /**
     * 省
     */
    @Column(name = "province_id")
    @CanEmpty
    private String provinceId;

    /**
     * 市
     */
    @Column(name = "city_id")
    @CanEmpty
    private String cityId;

    /**
     * 区
     */
    @Column(name = "area_id")
    @CanEmpty
    private String areaId;

    /**
     * 删除标志 0未删除 1已删除
     */
    @Column(name = "del_flag")
    @Enumerated
    private DeleteFlag delFlag;

    /**
     * 账号状态 0：启用中  1：禁用中
     */
    @Column(name = "customer_status")
    @Enumerated
    private CustomerStatus customerStatus;

    /**
     * 联系人名字
     */
    @Column(name = "contact_name")
    private String contactName;

    /**
     * 联系方式
     */
    @Column(name = "contact_phone")
    private String contactPhone;

    /**
     * 负责业务员
     */
    @Column(name = "employee_id")
    private String employeeId;

    /**
     * 创建时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "create_time")
    private LocalDateTime createTime;


    public CustomerDetailCopy(String customerId, String customerName, String provinceId, String cityId, String areaId, String contactPhone) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.provinceId = provinceId;
        this.cityId = cityId;
        this.areaId = areaId;
        this.contactPhone = contactPhone;
    }
}
