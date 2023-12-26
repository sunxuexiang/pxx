package com.wanmi.sbc.wallet.bean.vo;

import com.wanmi.sbc.wallet.bean.enums.PayWay;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>对账汇总返回结构</p>
 * Created by of628-wenzhi on 2017-12-11-下午3:01.
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountGatherVO implements Serializable {
    private static final long serialVersionUID = 2739572246730725651L;

    /**
     * 支付方式
     */
    @ApiModelProperty(value = "支付方式")
    private PayWay payWay;

    /**
     * 汇总金额，格式："￥#,###.00"
     */
    @ApiModelProperty(value = "汇总金额")
    private String sumAmount;

    /**
     * 百分比，格式："##.00%"
     */
    @ApiModelProperty(value = "百分比")
    private String percentage;
}
