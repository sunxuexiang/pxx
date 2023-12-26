package com.wanmi.sbc.customer.api.response.workorder;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.customer.bean.vo.WorkOrderVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>工单分页结果</p>
 * @author baijz
 * @date 2020-05-17 16:03:15
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderPageResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 工单分页结果
     */
    @ApiModelProperty(value = "工单分页结果")
    private MicroServicePage<WorkOrderVO> workOrderVOPage;
}
