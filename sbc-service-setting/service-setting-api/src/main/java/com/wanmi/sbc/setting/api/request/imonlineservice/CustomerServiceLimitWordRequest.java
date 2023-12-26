package com.wanmi.sbc.setting.api.request.imonlineservice;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerServiceLimitWordRequest extends BaseRequest {


    private Long wordId;

    /**
     * 类型：1、数字格式；2、过滤词语；3、特定数字格式
     */
    private Integer wordType;

    /**
     * 数字限制长度
     */
    private Integer digitLength;

    /**
     * 限制的词语内容
     */
    private String wordContent;

    /**
     * 描述
     */
    private String description;

    /**
     * 正则表达式
     */
    private String regex;
}
