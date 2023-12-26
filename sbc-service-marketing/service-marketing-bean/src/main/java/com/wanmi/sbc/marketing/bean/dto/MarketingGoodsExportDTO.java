package com.wanmi.sbc.marketing.bean.dto;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class MarketingGoodsExportDTO extends BaseQueryRequest {
    /**
     * 创建开始时间，精确到秒
     */
    @ApiModelProperty(value = "创建开始时间,精确到秒")
    private String startTime;

    /**
     * 创建结束时间，精确到秒
     */
    @ApiModelProperty(value = "创建结束时间,精确到秒")
    private String endTime;

    @ApiModelProperty(value = "查询的商品Erp多个")
    private String goodsErpNos;
}
