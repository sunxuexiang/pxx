package com.wanmi.sbc.returnorder.api.request.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @ClassName: HistoryLogisticCompanyRequest
 * @Description: TODO
 * @Date: 2020/11/12 9:30
 * @Version: 1.0
 */
@ApiModel
@Data
public class HistoryLogisticCompanyRequest implements Serializable {

    @ApiModelProperty(value = "时间")
    private LocalDateTime createTime;
    @ApiModelProperty(value = "电话")
    private String logisticsPhone;
    @ApiModelProperty(value = "订单号")
    private String orderId;
    @ApiModelProperty(value = "收获站点")
    private String receivingSite;
    @ApiModelProperty(value = "会员id")
    private String customerId;
    @ApiModelProperty(value = "公司名称")
    private String LogisticsName;
    @ApiModelProperty(value = "公司id")
    private Long companyId;
    @ApiModelProperty(value = "是否是客户自建物流")
    private Integer selFlag;
    @ApiModelProperty(value = "物流公司地址")
    private String LogisticsAddress;
    @ApiModelProperty(value = "物流类型")
    private Integer logisticsType;
    @ApiModelProperty(value = "批发市场ID")
    private Long marketId;
}
