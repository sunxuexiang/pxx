package com.wanmi.sbc.customer.usercontract.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "employee_contract")
public class EmployeeContract implements Serializable {

    /**
     * 主键
     */
    @Id
    @Column(name = "user_contract_id")
    private String userContractId;

    @Column(name = "employee_id")
    private String employeeId;

    @Column(name = "contractUrl")
    private String contractUrl;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "transaction_no")
    private String transactionNo;

    @Column(name = "status")
    private int status;

    @Column(name = "contract_id")
    private String contractId;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "supplier_name")
    private String supplierName;

    @Column(name = "sign_type")
    private Integer signType;

    @Column(name = "img_url")
    private String imgUrl;

    @Column(name = "investment_manager")
    private String investmentManager;

    @Column(name = "investemnt_manager_id")
    private String investemntManagerId;

    @Column(name = "is_person")
    private Integer isPerson;

    @Column(name = "create_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;
    @Column(name = "app_customer_id")
    private String appCustomerId;
    @Column(name = "app_id")
    private String appId;
}
