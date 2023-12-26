package com.wanmi.sbc.customer.api.response.workorderdetail;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * <p>工单明细修改结果</p>
 * @author baijz
 * @date 2020-05-17 16:03:58
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderCountrResponse implements Serializable {


    private static final long serialVersionUID = -9139052508074633552L;
    /**
     * 工单详情计数
     */
    @ApiModelProperty(value = "工单详情计数")
    private Map<String,Long> workDetalCount;
}
