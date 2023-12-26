package com.wanmi.sbc.customer.api.request.company;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @Author: songhanlin
 * @Date: Created In 下午3:23 2017/11/7
 * @Description: 商家类型Request
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyErpIdRequest extends BaseRequest {

    private static final long serialVersionUID = 6838306367156536511L;

    /**
     * 公司Id
     */
    @ApiModelProperty(value = "公司Id")
    @NotNull
    private Long companyInfoId;

    /**
     * erpId
     */
    @ApiModelProperty(value = "erpId")
    @NotNull
    private String erpId;
}
