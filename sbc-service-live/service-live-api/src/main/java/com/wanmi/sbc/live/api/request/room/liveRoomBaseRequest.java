package com.wanmi.sbc.live.api.request.room;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@ApiModel
@Data
public class liveRoomBaseRequest extends BaseRequest {



    /** 所有品牌账号 **/
    private Map<String, String> brandMap;


    /** 所有直播账号 **/
    private Map<String, String> accountMap;

    /** 所有运营账号 **/
    private Map<String, String> operationMap;
    
    /** 直播间图片 **/
    private String imgPath;

    /**
     * 直播账号手机号码
     */
    @ApiModelProperty(value = "直播账号手机号码")
    private List<String> livePhoneList;

    /** 直播间类型：0：非自营直播间；1、自营直播间；2：平台直播间 */
    @ApiModelProperty(value = "直播间类型：0：非自营直播间；1、自营直播间；2：平台直播间")
    private Integer roomType;
}
