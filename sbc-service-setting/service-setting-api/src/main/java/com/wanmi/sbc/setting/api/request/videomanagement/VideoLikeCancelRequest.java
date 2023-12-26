package com.wanmi.sbc.setting.api.request.videomanagement;

import com.wanmi.sbc.setting.bean.vo.VideoLikeVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
public class VideoLikeCancelRequest implements Serializable {

    /**
     * 用户点赞信息
     */
    @ApiModelProperty(value = "用户点赞信息")
    private VideoLikeVO videoLikeVO;
}
