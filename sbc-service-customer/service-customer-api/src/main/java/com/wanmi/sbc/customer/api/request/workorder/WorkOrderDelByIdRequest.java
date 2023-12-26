package com.wanmi.sbc.customer.api.request.workorder;

import com.wanmi.sbc.customer.api.request.CustomerBaseRequest;
import lombok.*;
import javax.validation.constraints.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>单个删除工单请求参数</p>
 * @author baijz
 * @date 2020-05-17 16:03:15
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderDelByIdRequest extends CustomerBaseRequest {
private static final long serialVersionUID = 1L;

    /**
     * 工单Id
     */
    @ApiModelProperty(value = "工单Id")
    @NotNull
    private String workOrderId;
}
