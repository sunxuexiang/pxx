package com.wanmi.sbc.setting.api.response.videoresourcecate;

import com.wanmi.sbc.setting.bean.vo.VideoResourceCateShortVO;
import com.wanmi.sbc.setting.bean.vo.VideoResourceCateVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 视频教程资源资源分类表列表结果
 * @author hudong
 * @date 2023-06-26 16:13:19
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoResourceCateListResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 视频教程资源资源分类表列表结果
     */
    @ApiModelProperty(value = "视频教程资源资源分类表列表结果")
    private List<VideoResourceCateVO> videoResourceCateVOList;
}
