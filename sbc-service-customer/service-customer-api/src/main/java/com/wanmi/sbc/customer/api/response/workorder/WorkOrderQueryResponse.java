package com.wanmi.sbc.customer.api.response.workorder;

import com.wanmi.sbc.customer.bean.vo.WorkOrderVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>工单修改结果</p>
 * @author baijz
 * @date 2020-05-17 16:03:15
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderQueryResponse implements Serializable {


    private static final long serialVersionUID = -7613769724083567028L;
    /**
     * 已修改的工单信息
     */
    @ApiModelProperty(value = "已修改的工单信息")
    private List<WorkOrderVO> workOrderVO;
}
