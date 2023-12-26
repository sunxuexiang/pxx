package com.wanmi.sbc.customer.api.response.merchantregistration;

import com.wanmi.sbc.customer.bean.vo.MerchantRegistrationVO;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 商家入驻申请信息查询
 * @author hudong
 * @date 2023-06-17 09:03
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
public class MerchantRegistrationByIdResponse extends MerchantRegistrationVO implements Serializable {


    private static final long serialVersionUID = -2426273139099738867L;
}
