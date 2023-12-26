package com.wanmi.sbc.customer.api.request.log;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description  
 * @author  shiy
 * @date    2023/4/8 9:28
 * @params  
 * @return  
*/
@ApiModel
@Data
public class CustomerLogPageRequest extends BaseQueryRequest {
    private static final long serialVersionUID = 1L;

    /**
     * 账号
     */
    private List<String> userNoList;

    /**
     * 账号
     */
    private String userNo;

    /**
     * app类型
     */
    private Integer appType;


    /**
     * app版本
     */
    private String appVersion;

    /**
     * 设备信息
     */
    private String devInfo;

    /**
     * 搜索条件:创建时间开始
     */
    private String createTimeBegin;
    /**
     * 搜索条件:创建时间截止
     */
    private String createTimeEnd;
}