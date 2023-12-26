package com.wanmi.sbc.marketing.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.marketing.bean.enums.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 领取记录
 * 应用场景：
 * 1、运营后台：应用-平台应用中心-优惠券列表
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponInfoGetRecordVO implements Serializable {


    @ApiModelProperty(value = "优惠券码id")
    private String couponCodeId;

    @ApiModelProperty(value = "用户账号-手机号")
    private String customerAccount;

    /**
     * 优惠券主键Id
     */
    @ApiModelProperty(value = "优惠券主键Id")
    private String couponId;

    /**
     * 优惠券名称
     */
    @ApiModelProperty(value = "优惠券名称")
    private String couponName;

    /**
     * 优惠券面值
     */
    @ApiModelProperty(value = "优惠券面值")
    private BigDecimal denomination;

    /**
     * 起止时间类型 0：按起止时间，1：按N天有效
     */
    @ApiModelProperty(value = "起止时间类型")
    private RangeDayType rangeDayType;

    /**
     * 优惠券开始时间
     */
    @ApiModelProperty(value = "优惠券开始时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime startTime;

    /**
     * 优惠券结束时间
     */
    @ApiModelProperty(value = "优惠券结束时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime endTime;

    /**
     * 有效天数
     */
    @ApiModelProperty(value = "有效天数")
    private Integer effectiveDays;

    /**
     * 领取数量
     */
    @ApiModelProperty(value = "领取数量")
    private Long receiveCount;


    @ApiModelProperty(value = "优惠券使用状态")
    private int useStatus;

    /**
     * 优惠券状态
     * 这里实际查询的是 券码的使用状态：coupon_code.use_status
     */
    @ApiModelProperty(value = "优惠券使用查询状态-字符")
    private String couponStatusStr;

    public String getCouponStatusStr(){
        return Optional.ofNullable(CouponCodeUseStatus.fromValue(useStatus)).map(m -> m.getName()).orElse("");
    }

    @ApiModelProperty(value = "有效期-组合判断")
    private String validityStr;

    public String getValidityStr(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (rangeDayType.toValue() == 0) {
            String startTimeStr = dtf.format(startTime);
            String endTimeStr = dtf.format(endTime);
            validityStr = startTimeStr + "至" + endTimeStr;
        }else{
            validityStr = "领取当天" + effectiveDays + "日内有效";
        }
        return validityStr;
    }

}
