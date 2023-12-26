package com.wanmi.sbc.returnorder.api.request.refundfreight;

import com.wanmi.sbc.common.base.Operator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/11/30 14:31
 */
@Data
@ApiModel
public class RefundFreightRequest implements Serializable {
    private static final long serialVersionUID = 7925427836084676557L;

    @ApiModelProperty("订单编号")
    @NotBlank
    private String tid;

    @ApiModelProperty("退运费金额")
    @NotNull
    private BigDecimal amount;

    @ApiModelProperty("备注")
    private String remarks;

    @ApiModelProperty(value = "操作人")
    private Operator operator;

}
