package com.wanmi.sbc.returnorder.api.request.returnorder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/12/7 11:39
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class ReturnLogisticsRequest implements Serializable {
    private static final long serialVersionUID = 329690463682200177L;

    @ApiModelProperty("店铺ID")
    @NotNull
    private Long storeId;

    @ApiModelProperty("订单ID")
    @NotNull
    private String tid;

    @ApiModelProperty("用户ID")
    private String customerId;
}
