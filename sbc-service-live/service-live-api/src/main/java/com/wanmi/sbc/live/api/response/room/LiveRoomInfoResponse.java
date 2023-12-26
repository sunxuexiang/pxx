package com.wanmi.sbc.live.api.response.room;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveRoomInfoResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    /**'直播间id**/
    private Long liveRoomId;

    /**'直播间名称**/
    private String liveRoomName;

    /**'平台标志，0：平台，1：非平台**/
    private Long sysFlag;

    /**'厂商id**/
    private Long companyId;

    private String companyName;
    
    private String imgPath;

    /** 店铺ID */
    private Long storeId;


//    /** 所有品牌账号 **/
//    private List<String> brandList;
//
//
//    /** 所有直播账号 **/
//    private List<String> accountList;
//
//    /** 所有运营账号 **/
//    private List<String> operationList;
    /** 所有品牌账号 key 为id ， value 为品牌名称 **/
    private Map<String, String> brandMap;
    /** 所有直播账号 key 为id ， value 为直播账号 **/
    private Map<String, String> accountMap;
    /** 所有运营账号  key 为id ， value 为运营账号**/
    private Map<String, String> operationMap;

    /**
     * 最近直播时间
     */
    private String lastLiveTime;
}
