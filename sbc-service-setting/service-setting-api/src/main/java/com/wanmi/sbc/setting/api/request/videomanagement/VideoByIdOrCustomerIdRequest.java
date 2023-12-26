package com.wanmi.sbc.setting.api.request.videomanagement;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * <p>描述<p>
 *
 * @author zhaowei
 * @date 2021/4/19
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoByIdOrCustomerIdRequest {
    /**
     * videoID
     */
    @ApiModelProperty(value = "ID")
    private Long videoId;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户id")
    private String customerId;
}
