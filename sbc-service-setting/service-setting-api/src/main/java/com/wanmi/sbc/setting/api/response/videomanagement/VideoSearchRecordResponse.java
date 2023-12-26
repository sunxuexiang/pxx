package com.wanmi.sbc.setting.api.response.videomanagement;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>视频搜索记录</p>
 * @author zzg
 * @date 2023-09-17 17:47:22
 */
@Data
public class VideoSearchRecordResponse {

    private Long recordId;

    /**
     * 客户ID
     */
    @ApiModelProperty(value = "客户ID")
    private String customerId;

    /**
     * 搜索类容
     */
    @ApiModelProperty(value = "搜索类容")
    private String content;

    /**
     * 搜索时间
     */
    @ApiModelProperty(value = "搜索时间")
    private Long searchTime;
}
