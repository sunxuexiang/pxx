package com.wanmi.sbc.customer.api.response.workorder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>根据id查询任意（包含已删除）工单信息response</p>
 * @author baijz
 * @date 2020-05-17 16:03:15
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderExistByRegisterCustomerIdResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * true false
     */
    @ApiModelProperty(value = "是否存在未处理的工单")
    private Boolean existFlag;
}
