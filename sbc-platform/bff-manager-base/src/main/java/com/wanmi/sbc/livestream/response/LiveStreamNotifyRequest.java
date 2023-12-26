package com.wanmi.sbc.livestream.response;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 录制回调实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class LiveStreamNotifyRequest {

    /**
     * 用户 APPID
     */
    private int appid;


    /**
     * 推流域名
     */
    private String app;


    /**
     * 推流路径
     */
    private String appname;

    /**
     * 直播流名称
     */
    private String stream_id;

    /**
     * 录制时长
     */
    private Long duration;
    /**
     * 录制文件下载 URL
     */
    private String video_url;

    private String sign;

    private Long t;

    /**
     * 推断流错误码
     */
    private Integer errcode;

    /**
     * 推断流错误描述
     */
    private String errmsg;

    /**
     * 0 直播断流 1 直播推流
     */
    private Integer event_type;

    /**
     * 事件消息产生的 UNIX 时间戳
     */
    private Integer event_time;
}
