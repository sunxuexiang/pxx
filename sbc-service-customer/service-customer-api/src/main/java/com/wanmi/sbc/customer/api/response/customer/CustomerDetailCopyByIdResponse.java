package com.wanmi.sbc.customer.api.response.customer;

import com.wanmi.sbc.customer.bean.vo.CustomerDetailCopyVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author lm
 * @date 2022/09/20 20:37
 */
@Data
public class CustomerDetailCopyByIdResponse implements Serializable {
    private static final long serialVersionUID = -4561855333716527650L;
    /**
     * 会员信息列表
     */
    @ApiModelProperty(value = "会员信息")
    private List<CustomerDetailCopyVO> detailResponseList;
}
