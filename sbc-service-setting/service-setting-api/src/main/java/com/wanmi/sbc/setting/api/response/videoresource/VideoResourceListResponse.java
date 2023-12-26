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

/**
 * 视频教程资源库列表结果
 * @author hudong
 * @date 2023-06-26 16:12:49
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoResourceListResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 视频教程二级分类名
     */
    private String cateName;

    /**
     * 视频教程资源库列表结果
     */
    @ApiModelProperty(value = "视频教程资源库列表结果")
    private List<VideoResourceVO> videoResourceVOList;
}
