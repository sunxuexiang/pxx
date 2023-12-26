package com.wanmi.sbc.live.api.response.room;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveDetailExportVo  implements Serializable {

    private Long storeId;

    private String storeName;

    /**
     * 直播间名称
     */
    private String roomName;


    private Long companyId;

    /**
     * 厂商名称
     */
    private String companyName;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 开播时段（时分秒）
     */
    private String liveTime;

    /**
     * 开播日期（年月日）
     */
    private String liveDate;

    /**
     * 开播时段（时分秒）
     */
    private String liveHour;

    /**
     * 直播时长
     */
    private Integer liveDuration;

    /**
     * 直播账号
     */
    private String customerAccount;

    /**
     * 直播回放地址
     */
    private String mediaUrl;
}
