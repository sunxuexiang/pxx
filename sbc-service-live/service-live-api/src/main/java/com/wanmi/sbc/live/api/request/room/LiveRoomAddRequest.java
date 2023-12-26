package com.wanmi.sbc.live.api.request.room;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveRoomAddRequest extends liveRoomBaseRequest {



    /**'直播间名称**/
    @NotBlank
    private String liveRoomName;

    /**'平台标志，0：平台，1：非平台**/
    private Long sysFlag;

    /**'厂商id**/
    private Long companyId;

    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 直播账号手机号码
     */
    private List<String> livePhoneList;

}
