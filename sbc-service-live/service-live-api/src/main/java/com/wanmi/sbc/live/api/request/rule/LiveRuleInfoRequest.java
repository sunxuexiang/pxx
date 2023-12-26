package com.wanmi.sbc.live.api.request.rule;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveRuleInfoRequest extends BaseRequest {
    private static final long serialVersionUID = 1L;

    private Integer liveRoomId;

    @ApiModelProperty(value = "规则类型")
    private Integer type;
}
