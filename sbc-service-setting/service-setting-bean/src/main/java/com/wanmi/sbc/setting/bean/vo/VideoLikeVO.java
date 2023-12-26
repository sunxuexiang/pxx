package com.wanmi.sbc.setting.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>描述<p>
 *
 * @author zhaowei
 * @date 2021/4/19
 */
@ApiModel
@Data
public class VideoLikeVO implements Serializable {
    /**
     * videoID
     */
    @ApiModelProperty(value = "ID")
    @NotNull
    private Long videoId;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户id")
    @NotNull
    private String customerId;

}
