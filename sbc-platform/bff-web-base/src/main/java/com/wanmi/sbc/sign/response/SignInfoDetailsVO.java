package com.wanmi.sbc.sign.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignInfoDetailsVO {

    @ApiModelProperty(value = "签到日期")
    private String signDay;

    @ApiModelProperty(name = "签到天数")
    private Integer signDays;

    @ApiModelProperty(value = "是否签到 0 否 1 是")
    private Integer isSign;

    @ApiModelProperty(value = "该日期是否可以签到 0 否 1 是")
    private Integer isThatOK;

    @ApiModelProperty(value = "签到获得的优惠券名称")
    private String couponName;

    @ApiModelProperty(value = "优惠券面值")
    private BigDecimal denomination;

    @ApiModelProperty(value = "签到获得的优惠券数量")
    private Long totalCount;
}
