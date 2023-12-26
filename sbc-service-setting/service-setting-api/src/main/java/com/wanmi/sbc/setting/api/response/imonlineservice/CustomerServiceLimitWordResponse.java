package com.wanmi.sbc.setting.api.response.imonlineservice;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerServiceLimitWordResponse implements Serializable {

    private Long wordId;

    /**
     * 类型：1、数字长度；2、过滤词语；3、特定数字格式
     */
    private Integer wordType;


    /**
     * 描述
     */
    private String description;

    /**
     * 限制的词语内容
     */
    private String wordContent;

    /**
     * 正则表达式
     */
    private String regex;

}
