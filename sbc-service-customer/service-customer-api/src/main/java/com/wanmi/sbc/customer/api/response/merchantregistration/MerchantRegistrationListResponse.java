package com.wanmi.sbc.customer.api.response.merchantregistration;

import com.wanmi.sbc.customer.bean.vo.MerchantRegistrationVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * 商家入驻申请信息查询
 * @author hudong
 * @date 2023-06-17 09:03
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MerchantRegistrationListResponse implements Serializable {


    private static final long serialVersionUID = -2426273139099738867L;
    /**
     * 商家入驻申请信息列表
     */
    @ApiModelProperty(value = "商家入驻申请信息列表")
    private List<MerchantRegistrationVO> merchantRegistrationVOList;
}
