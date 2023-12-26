package com.wanmi.sbc.setting.api.response.videomanagement;

import com.wanmi.sbc.setting.bean.vo.VideoManagementVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>视频管理列表结果</p>
 * @author zhaowei
 * @date 2021-04-17 17:47:22
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoManagementListResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 视频管理列表结果
     */
    @ApiModelProperty(value = "视频管理列表结果")
    private List<VideoManagementVO> videoManagementVOList;
}
