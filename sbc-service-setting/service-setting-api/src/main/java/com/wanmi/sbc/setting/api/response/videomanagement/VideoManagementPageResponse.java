package com.wanmi.sbc.setting.api.response.videomanagement;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.setting.bean.vo.VideoManagementVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>视频管理分页结果</p>
 * @author zhaowei
 * @date 2021-04-17 17:47:22
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoManagementPageResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 视频管理分页结果
     */
    @ApiModelProperty(value = "视频管理分页结果")
    private MicroServicePage<VideoManagementVO> videoManagementVOPage;
}
