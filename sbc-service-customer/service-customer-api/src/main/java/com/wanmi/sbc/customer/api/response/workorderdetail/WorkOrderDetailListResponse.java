package com.wanmi.sbc.customer.api.response.workorderdetail;

import com.wanmi.sbc.customer.bean.vo.WorkOrderDetailVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>工单明细列表结果</p>
 * @author baijz
 * @date 2020-05-17 16:03:58
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderDetailListResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 工单明细列表结果
     */
    @ApiModelProperty(value = "工单明细列表结果")
    private List<WorkOrderDetailVO> workOrderDetailVOList;
}
