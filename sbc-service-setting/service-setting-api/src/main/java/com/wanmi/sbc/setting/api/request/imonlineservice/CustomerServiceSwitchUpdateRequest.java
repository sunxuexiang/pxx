package com.wanmi.sbc.setting.api.request.imonlineservice;

import com.wanmi.sbc.common.enums.CustomerServiceType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 修改客服服务类型开关请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerServiceSwitchUpdateRequest implements Serializable {

    /**
     * 公司ID
     */
    private Long companyId;

    /**
     * 开关状态：0、关闭；1、开启
     */
    @ApiModelProperty(value = "开关状态：0、关闭；1、开启")
    private Integer switchStatus = 0;

    /**
     * 客服类型
     */
    private CustomerServiceType customerServiceType;

}
