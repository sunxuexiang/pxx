package com.wanmi.sbc.setting.imonlineservice.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "customer_service_limit_word")
public class CustomerServiceLimitWord {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "word_id")
    private Long wordId;

    /**
     * 类型：1、数字长度；2、过滤词语；3、特定数字格式
     */
    @Column(name = "word_type")
    private Integer wordType;

    /**
     * 限制的词语内容
     */
    @Column(name = "word_content")
    private String wordContent;

    /**
     * 描述
     */
    @Column(name = "description")
    private String description;

    /**
     * 正则表达式
     */
    @Column(name = "regex")
    private String regex;

    @Column(name = "create_time")
    private LocalDateTime createTime;
}
