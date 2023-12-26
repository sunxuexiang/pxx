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
public class LiveStreamInfoRequest extends BaseRequest {
    private static final long serialVersionUID = 1L;
    /**
     * 直播id
     */
    @ApiModelProperty(value = "直播liveId")
    private Integer liveId;

    @ApiModelProperty(value = "直播间liveRoomId")
    private Integer liveRoomId;
    /**
     * 直播状态
     */
    @ApiModelProperty(value = "直播状态")
    private Integer liveStatus;

    /**
     * 直播分享用户
     */
    @ApiModelProperty(value = "直播间分享用户")
    private String sharerAccount;

    /**
     * 直播流名称
     */
    @ApiModelProperty(value = "直播流名称")
    private String streamName;

    @ApiModelProperty(value = "是否官方直播间")
    private Integer sysFlag;

    @ApiModelProperty(value = "开播设备标识")
    private String sourceChannel;

    @ApiModelProperty(value = "商品 sku Id")
    private String goodsInfoId;

    /** 直播间类型：0：非自营直播间；1、自营直播间；2：平台直播间 */
    private Integer roomType;

    @ApiModelProperty(value = "店铺ID")
    private Long storeId;
}
