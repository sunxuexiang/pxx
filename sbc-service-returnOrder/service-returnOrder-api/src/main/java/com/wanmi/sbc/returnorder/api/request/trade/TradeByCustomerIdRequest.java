package com.wanmi.sbc.returnorder.api.request.trade;

import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.setting.bean.enums.LogisticsType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

/**
 * 作者: fcq
 * 日期: 2020/11/9 15:00
 * 描述:
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class TradeByCustomerIdRequest extends BaseRequest {
    private static final long serialVersionUID = -9143745241530996245L;

    @ApiModelProperty(value = "会员id")
    private String customerId;

    @ApiModelProperty(value = "会员id集合")
    private List<String> customerIds;
    @ApiModelProperty(value = "物流类型")
    private Integer logisticsType;
    @ApiModelProperty(value = "批发市场ID")
    private Long marketId;

    public Integer getLogisticsType() {
        if(this.logisticsType==null){
            return LogisticsType.THIRD_PARTY_LOGISTICS.toValue();
        }
        return logisticsType;
    }
    public Long getMarketId() {
        if(this.marketId==null){
            return 190L;//生产长沙批发市场
        }
        return marketId;
    }

}
