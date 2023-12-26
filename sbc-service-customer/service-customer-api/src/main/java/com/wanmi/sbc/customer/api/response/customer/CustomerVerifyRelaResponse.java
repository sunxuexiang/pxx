package com.wanmi.sbc.customer.api.response.customer;

import com.wanmi.sbc.customer.bean.dto.CustomerVerifyRelaDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @program: sbc_h_tian
 * @description: 校验是否能够绑定账户为子账户
 * @author: Mr.Tian
 * @create: 2020-06-05 13:48
 **/
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerVerifyRelaResponse  implements Serializable {
    private static final long serialVersionUID = -7738037981104056619L;
    /**
     * 不能绑定的手机号码
     */
    @ApiModelProperty(value = "不能绑定的手机号码")
    private List <CustomerVerifyRelaDTO> customerList;

    /**
     * 在工单中的会员
     */
    @ApiModelProperty(value = "在工单中的会员")
    private List<String> inWorkOrders;

    /**
     * 不是会员的手机号
     */
    @ApiModelProperty(value = "不是会员的手机号")
    private List<String> noCustomers;
}
