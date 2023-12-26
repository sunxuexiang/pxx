package com.wanmi.sbc.live.api.request.activity;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

/**
 * <p>直播活动新增参数</p>
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveBagAddRequest extends BaseRequest {
    private static final long serialVersionUID = 1L;
    /**
     * 福袋id
     */
    @ApiModelProperty(value = "福袋id")
    private Integer bagId;
}
