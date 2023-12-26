package com.wanmi.sbc.customer.api.request.store;

import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.customer.api.request.CustomerBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * <p>根据id查询单个公司信息request</p>
 * Created by daiyitian on 2018-08-13-下午3:47.
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreByCompanyTypeRequest extends CustomerBaseRequest {

    private static final long serialVersionUID = 2304827735774417750L;
    /**
     * 商家类型
     */
    @ApiModelProperty(value = "商家类型")
    @NotNull
    private CompanyType companyType;

}
