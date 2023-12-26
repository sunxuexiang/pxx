package com.wanmi.sbc.setting.hotstylemoments.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseEntity;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.setting.bean.enums.HotStyleMomentsStatus;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description: 爆款时刻信息实体类
 * @author: XinJiang
 * @time: 2022/5/9 17:47
 */
@Data
@Entity
@Table(name = "hot_style_moments")
public class HotStyleMoments extends BaseEntity {

    private static final long serialVersionUID = 3870067294959241902L;

    /**
     * 主键
     */
    @Id
    @GeneratedValue
    @Column(name = "hot_id")
    private Long hotId;

    /**
     * 爆款时刻主题名称
     */
    @Column(name = "hot_name")
    private String hotName;

    /**
     * 开始时间
     */
    @Column(name = "begin_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime beginTime;

    /**
     * 结束时间
     */
    @Column(name = "end_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime endTime;

    /**
     * 详情页banner图标地址
     */
    @Column(name = "banner_image_url")
    private String bannerImageUrl;

    /**
     * 是否暂停（1：暂停，0：正常）
     */
    @Column(name = "is_pause")
    @Enumerated
    private DefaultFlag isPause = DefaultFlag.NO;

    /**
     * 终止标志位：1：终止，0非终止
     */
    @Column(name = "termination_flag")
    @Enumerated
    private DefaultFlag terminationFlag = DefaultFlag.NO;

    /**
     * 删除标记  0：正常，1：删除
     */
    @Column(name = "del_flag")
    @Enumerated
    private DeleteFlag delFlag = DeleteFlag.NO;

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
     * 商品配置信息列表
     */
    @OneToMany
    @JoinColumn(name = "hot_id", insertable = false, updatable = false)
    @JsonIgnore
    private List<HotStyleMomentsConfig> hotStyleMomentsConfigs;

    /**
     * 状态
     */
    @Transient
    private HotStyleMomentsStatus status;
}
