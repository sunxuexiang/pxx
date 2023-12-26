package com.wanmi.sbc.datecenter.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author lm
 * @date 2022/09/17 11:05
 */
@ApiModel("boss权限数据统计参数")
@Data
public class DataCenterStatisticForBossRequest {

    @ApiModelProperty("时间")
    private String date;

    @ApiModelProperty("省code")
    private String provinceCode;

    @ApiModelProperty("排序类型；0：按下单笔数排行（默认），1：按下单箱数排行")
    private Integer sortedType = 0;

    @ApiModelProperty("页数")
    private Integer page = 1;// 页数

    @ApiModelProperty("页面大小")
    private Integer pageSize = 30;
}
