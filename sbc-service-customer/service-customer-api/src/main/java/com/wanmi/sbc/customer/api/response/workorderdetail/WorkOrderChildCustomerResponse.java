package com.wanmi.sbc.customer.api.response.workorderdetail;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

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
public class WorkOrderChildCustomerResponse implements Serializable {


    private static final long serialVersionUID = 2169222472263619079L;
    /**
     * 已修改的工单明细信息
     */
    @ApiModelProperty(value = "已修改的工单明细信息")
    private List<String> customerIds;
}
