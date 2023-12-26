package com.wanmi.sbc.customer.company.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-06-14 11:35
 **/
@Setter
@Getter
@ToString
@Entity
@Table(name = "company_mall_supplier_tab")
public class CompanyMallSupplierTab implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tab_name")
    private String tabName;

    /**
     * 创建时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 操作人
     */
    @Column(name = "operator")
    private String operator;

    /**
     * 修改时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 删除标志 0未删除 1已删除
     */
    @Column(name = "del_flag")
    @Enumerated
    private DeleteFlag delFlag;

    @Column(name = "open_status")
    private Integer openStatus;

    @Column(name = "sort")
    private BigDecimal sort;

    @Column(name = "store_ids")
    private String storeIds;

    @Column(name = "delivery_types")
    private String deliveryTypes;

    @Column(name = "rel_cat_id")
    private Long relCatId;
}
