package com.wanmi.sbc.live.api.request.stream;

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
public class BagAppRequest extends BaseRequest {
    private static final long serialVersionUID = 1L;
    /**
     * 账号id
     */
    @ApiModelProperty(value = "账号id")
    private String customerId;

    /**
     * 账号
     */
    @ApiModelProperty(value = "账号")
    private String customerAccount;

    /**
     * 福袋Id
     */
    @ApiModelProperty(value = "福袋id")
    private Integer bagId;

    @ApiModelProperty(value = "直播记录id")
    private Integer liveId;

    /**
     * 发放状态
     */
    @ApiModelProperty(value = "发放状态")
    private  Integer ticketStatus;
}
