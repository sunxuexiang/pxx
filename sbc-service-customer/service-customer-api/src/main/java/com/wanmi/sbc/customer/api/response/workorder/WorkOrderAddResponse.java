package com.wanmi.sbc.customer.api.response.workorder;

import com.wanmi.sbc.customer.bean.vo.WorkOrderVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>工单新增结果</p>
 * @author baijz
 * @date 2020-05-17 16:03:15
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderAddResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 已新增的工单信息
     */
    @ApiModelProperty(value = "已新增的工单信息")
    private WorkOrderVO workOrderVO;
}
