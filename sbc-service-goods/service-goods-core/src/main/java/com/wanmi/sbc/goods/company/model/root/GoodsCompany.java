package com.wanmi.sbc.goods.company.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品厂商实体类
 * Created by dyt on 2017/4/11.
 */
@Data
@Entity
@Table(name = "goods_company")
public class GoodsCompany implements Serializable {
    /**
     * 厂商编号
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    private Long companyId;

    /**
     * 厂商名称
     */
    @Column(name = "company_name")
    private String companyName;


    /**
     * 联系方式
     */
    @Column(name = "contact_phone")
    private String contactPhone;

    /**
     * 联系地址
     */
    @Column(name = "contact_address")
    private String contactAddress;

    /**
     * 品牌id
     */
    @Column(name = "brand_ids")
    private String brandIds;

    /**
     * 品牌名称
     */
    @Column(name = "brand_names")
    private String brandNames;

    /**
     * 状态 0 启用 1 禁用
     */
    @Column(name = "status")
    private Integer status;
    /**
     * 创建时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 删除标志
     */
    @Column(name = "del_flag")
    @Enumerated
    private DeleteFlag delFlag;
}
