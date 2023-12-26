package com.wanmi.sbc.setting.api.response.videoresource;

import com.wanmi.sbc.setting.bean.vo.VideoResourceVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 视频教程资源库分页结果
 * @author hudong
 * @date 2023-06-26 16:12:49
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoResourceMapResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 视频教程资源库分页结果
     */
    @ApiModelProperty(value = "视频教程资源库分页结果")
    private Map<String, List<VideoResourceVO>> videoResourceVOMap;
}
