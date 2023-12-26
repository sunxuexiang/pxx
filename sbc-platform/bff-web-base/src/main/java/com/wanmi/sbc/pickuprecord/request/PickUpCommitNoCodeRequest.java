package com.wanmi.sbc.pickuprecord.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *
 * @author baijianzhong
 * @ClassName MatchWareHouseRequest
 * @Date 2020-06-01 16:12
 * @Description TODO 根据市code匹配分仓
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class PickUpCommitNoCodeRequest implements Serializable {


    private static final long serialVersionUID = -6808545751100530871L;
    /**
     * 订单ID
     */
    @ApiModelProperty(value = "订单id")
    @NotNull
    private String tradeId;
}
