package com.wanmi.sbc.customer.company.model.root;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
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
@Table(name = "company_mall_return_goods_address")
public class CompanyMallReturnGoodsAddress implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_info_id")
    private Long companyInfoId;

    @Column(name = "store_id")
    private Long storeId;

    @Column(name = "province_id")
    private Long provinceId;

    @Column(name = "city_id")
    private Long cityId;

    @Column(name = "area_id")
    private Long areaId;

    @Column(name = "town_id")
    private Long townId;

    @Column(name = "receive_name")
    private String receiveName;

    @Column(name = "receive_phone")
    private String receivePhone;

    @Column(name = "detail_address")
    private String detailAddress;

    /**
     * 商家Id
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "company_info_id", updatable = false, insertable = false)
    @NotFound(action= NotFoundAction.IGNORE)
    private CompanyInfo companyInfo;

//    /**
//     * 公司信息
//     */
//    @ManyToOne()
//    @JoinColumn(name = "company_info_id",insertable = false, updatable = false)
//    @JsonBackReference
//    private CompanyInfo companyInfo;
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
}
