package com.wanmi.sbc.order.response;

import com.wanmi.sbc.common.enums.DefaultFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: gaomuwei
 * @Date: Created In 下午5:58 2018/9/28
 * @Description: 订单确认返回结构
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryHomeFlagResponse {

    /**
     * 是否满足送货到家标志位
     */
    @ApiModelProperty(value = "是否满足送货到家标志位，0：不满足，1：满足")
    private DefaultFlag flag;

    @ApiModelProperty(value = "如果是湖北地址且不是免店配就返回最最近战点地址")
    private String adress;

    @ApiModelProperty(value = "联系人")
    private String contacts;

    @ApiModelProperty(value = "网点id")
    private Long networkId;

    @ApiModelProperty(value = "网点电话号码")
    private String phone;

    @ApiModelProperty(value = "网点名称")
    private String networkName;


}