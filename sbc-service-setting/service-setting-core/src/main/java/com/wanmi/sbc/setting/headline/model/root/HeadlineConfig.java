package com.wanmi.sbc.setting.headline.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 白鲸头条
 */
@Data
@Entity
@Table(name = "headline_config")
public class HeadlineConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 头条ID
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "headline_id", nullable = false)
    private String headlineId;

    /**
     * 内容
     */
    @Column(name = "content")
    private String content;

    /**
     * 排序
     */
    @Column(name = "sort_num")
    private Integer sortNum;

    /**
     * 总秒数
     */
    @Column(name = "speeds")
    private BigDecimal speeds;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 创建人id
     */
    @Column(name = "create_person")
    private String createPerson;

    /**
     * 更新人id
     */
    @Column(name = "update_person")
    private String updatePerson;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

}
