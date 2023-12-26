package com.wanmi.sbc.customer.api.request.merchantregistration;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商家入驻申请修改信息参数
 * @author hudong
 * @date 2023-06-19 09:03
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
public class MerchantRegistrationListRequest extends BaseQueryRequest {

    private static final long serialVersionUID = 6518396307249530736L;
    /**
     * 申请id列表
     */
    @ApiModelProperty(value = "申请id列表")
    private List<Long> applicationIds;

    /**
     * 商家名称
     */
    @ApiModelProperty(value = "商家名称")
    private String merchantName;

    /**
     * 公司名称
     */
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    /**
     * 商家联系方式
     */
    @ApiModelProperty(value = "商家联系方式")
    private String merchantPhone;

    /**
     * 对接人
     */
    @ApiModelProperty(value = "对接人")
    private String contactPerson;


    /**
     * 搜索条件:申请时间开始
     */
    @ApiModelProperty(value = "搜索条件:申请时间开始")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTimeBegin;
    /**
     * 搜索条件:申请时间截止
     */
    @ApiModelProperty(value = "搜索条件:申请时间截止")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTimeEnd;


    /**
     * 搜索条件:处理时间开始
     */
    @ApiModelProperty(value = "搜索条件:处理时间开始")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTimeBegin;
    /**
     * 搜索条件:处理时间截止
     */
    @ApiModelProperty(value = "搜索条件:处理时间截止")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTimeEnd;

    /**
     * 是否处理
     */
    @ApiModelProperty(value = "是否处理 0 未处理 1 已处理")
    private Integer handleFlag;

    @ApiModelProperty(value = "是否删除 0 否 1 是")
    private DeleteFlag deleteFlag;


}
