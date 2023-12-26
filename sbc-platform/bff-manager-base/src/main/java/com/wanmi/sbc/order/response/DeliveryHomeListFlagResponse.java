package com.wanmi.sbc.order.response;

import com.wanmi.sbc.common.enums.DefaultFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @Author: gaomuwei
 * @Date: Created In 下午5:58 2018/9/28
 * @Description: 订单确认返回结构
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryHomeListFlagResponse {

    /**
     * 是否满足送货到家标志位
     */
    @ApiModelProperty(value = "是否满足送货到家标志位，0：不满足，1：满足")
    private Map<Integer, DefaultFlag> flagMap;


}