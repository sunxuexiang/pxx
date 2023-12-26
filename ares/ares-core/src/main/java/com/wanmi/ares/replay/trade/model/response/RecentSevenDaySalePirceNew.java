package com.wanmi.ares.replay.trade.model.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Administrator
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class RecentSevenDaySalePirceNew implements Serializable {

    @ApiModelProperty(value = "省份ID")
    private String provinceId;

    @ApiModelProperty(value = "省份名称")
    private String provinceName;

    private static final long serialVersionUID = 6561614418028293116L;
    @ApiModelProperty(value = "日期")
    private String dayTime;

    @ApiModelProperty(value = "金额")
    private BigDecimal daySalePrice;

}
