package com.wanmi.sbc.customer.api.response.detail;

import com.wanmi.sbc.customer.bean.vo.CustomerDetailVO;
import com.wanmi.sbc.customer.bean.vo.StockOutCustomerVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 会员信息响应
 * Created by CHENLI on 2017/4/19.
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerStockOutListResponse implements Serializable {

    private static final long serialVersionUID = -8199155970686688626L;
    /**
     * 会员信息列表
     */
    @ApiModelProperty(value = "会员信息列表")
    private List<StockOutCustomerVO> customerDetailVOList;
}
