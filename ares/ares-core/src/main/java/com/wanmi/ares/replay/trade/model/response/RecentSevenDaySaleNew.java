package com.wanmi.ares.replay.trade.model.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class RecentSevenDaySaleNew implements Serializable {

    private static final long serialVersionUID = 1214604338408703348L;

    @ApiModelProperty(value = "省份ID")
    private String provinceId;

    @ApiModelProperty(value = "省份名称")
    private String provinceName;

    @ApiModelProperty(value = "日期")
    private String dayTime;

    @ApiModelProperty(value = "箱数")
    private Long totalNum;

}
