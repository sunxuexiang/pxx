package com.wanmi.sbc.customer.api.response.customer;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.customer.bean.vo.CustomerDetailVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 会员信息响应
 * Created by CHENLI on 2017/4/19.
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerDetailListPageResponse implements Serializable {

    private static final long serialVersionUID = -7009565249197503212L;
    /**
     * 会员信息列表
     */
    @ApiModelProperty(value = "会员信息列表")
    private MicroServicePage<CustomerDetailVO> detailResponsePage;
}
