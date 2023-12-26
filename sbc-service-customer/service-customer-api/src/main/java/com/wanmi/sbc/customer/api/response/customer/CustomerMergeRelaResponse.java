package com.wanmi.sbc.customer.api.response.customer;

import com.wanmi.sbc.customer.bean.vo.CustomerMergeVO;
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
 * @description: 批量绑定子账户结果
 * @author: Mr.Tian
 * @create: 2020-06-03 14:27
 **/
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerMergeRelaResponse implements Serializable {


    private static final long serialVersionUID = -964053320020090215L;
    @ApiModelProperty(value = "已被绑定的手机号码")
    private List<CustomerMergeVO> customerMergeVOS;

}
