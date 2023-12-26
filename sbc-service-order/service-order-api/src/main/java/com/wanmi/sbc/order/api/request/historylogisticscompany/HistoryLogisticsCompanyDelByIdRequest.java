package com.wanmi.sbc.order.api.request.historylogisticscompany;

import com.wanmi.sbc.order.api.request.OrderBaseRequest;
import lombok.*;
import javax.validation.constraints.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>单个删除物流信息历史记录请求参数</p>
 * @author fcq
 * @date 2020-11-09 17:32:23
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryLogisticsCompanyDelByIdRequest extends OrderBaseRequest {
private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @NotNull
    private String id;
}
