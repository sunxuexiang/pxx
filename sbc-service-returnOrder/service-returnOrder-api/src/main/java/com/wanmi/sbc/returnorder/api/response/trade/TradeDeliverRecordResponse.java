package com.wanmi.sbc.returnorder.api.response.trade;

import com.wanmi.sbc.returnorder.bean.vo.LogisticsInfoVO;
import com.wanmi.sbc.returnorder.bean.vo.TradeDeliverVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 * author: sunkun
 * Date: 2018-12-11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel
public class TradeDeliverRecordResponse implements Serializable {

    private static final long serialVersionUID = 8094523587595462766L;

    /**
     * 发货记录
     */
    @ApiModelProperty(value = "发货记录列表")
    private List<TradeDeliverVO> tradeDeliver = new ArrayList<>();

    /**
     * 订单总体状态
     */
    @ApiModelProperty(value = "订单总体状态")
    private String status;

    /**
     *物流公司信息(如果配送方式是物流则存在)
     */
    @ApiModelProperty(value = "物流公司信息(如果配送方式是物流则存在)")
    private LogisticsInfoVO logisticsCompanyInfo;
}
