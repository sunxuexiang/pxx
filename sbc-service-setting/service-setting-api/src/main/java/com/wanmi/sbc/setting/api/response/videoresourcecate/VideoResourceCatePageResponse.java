package com.wanmi.sbc.setting.api.response.videoresourcecate;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.setting.bean.vo.VideoResourceCateVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 视频教程资源资源分类表分页结果
 * @author hudong
 * @date 2023-06-26 16:13:19
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoResourceCatePageResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 视频教程资源资源分类表分页结果
     */
    @ApiModelProperty(value = "视频教程资源资源分类表分页结果")
    private MicroServicePage<VideoResourceCateVO> videoResourceCateVOPage;
}
