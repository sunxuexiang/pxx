package com.wanmi.sbc.setting.advertising.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.ms.util.CustomLocalDateDeserializer;
import com.wanmi.ms.util.CustomLocalDateSerializer;
import com.wanmi.sbc.common.base.BaseEntity;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @description: 首页广告位实体类
 * @author: XinJiang
 * @time: 2022/2/18 9:28
 */
@Data
@Entity
@Table(name = "start_page_advertising")
public class StartPageAdvertising extends BaseEntity {

    private static final long serialVersionUID = 2100757983028274411L;

    /**
     * 主键id
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "advertising_id")
    private String advertisingId;

    /**
     * 广告名称
     */
    @Column(name = "advertising_name")
    private String advertisingName;

    /**
     * 背景颜色
     */
    @Column(name = "background_color")
    private String backgroundColor;

    /**
     * 图片地址
     */
    @Column(name = "image_url")
    private String imageUrl;

    /**
     * 是否设置跳转链接，0-否，1-是
     */
    @Enumerated
    @Column(name = "link_flag")
    private DefaultFlag linkFlag = DefaultFlag.NO;

    /**
     * 魔方海报页名称
     */
    @Column(name = "mofang_advertising_name")
    private String mofangName;

    /**
     * 魔方海报页编号
     */
    @Column(name = "mofang_page_code")
    private String mofangCode;

    /**
     * 生效类型 0：立即生效，1：固定时间
     */
    @Enumerated
    @Column(name = "effect_type")
    private DefaultFlag effectType = DefaultFlag.NO;

    /**
     * 生效时间
     */
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @Column(name = "effect_date")
    private LocalDate effectDate;

    /**
     * 状态，0关闭 1开启
     */
    @Enumerated
    @Column(name = "status")
    private DefaultFlag status = DefaultFlag.NO;

    /**
     * 删除标志 0否 1是
     */
    @Enumerated
    @Column(name = "del_flag")
    private DeleteFlag delFlag = DeleteFlag.NO;

    /**
     * 删除时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @Column(name = "del_time")
    private LocalDateTime delTime;

    /**
     * 删除人
     */
    @Column(name = "del_person")
    private String delPerson;

}
