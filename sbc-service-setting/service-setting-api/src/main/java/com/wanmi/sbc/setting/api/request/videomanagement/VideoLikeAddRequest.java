package com.wanmi.sbc.setting.api.request.videomanagement;

import com.wanmi.sbc.setting.bean.vo.VideoLikeVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoLikeAddRequest  implements Serializable {

    /**
     * 用户点赞信息
     */
    @ApiModelProperty(value = "用户点赞信息")
    private VideoLikeVO videoLikeVO;
}
