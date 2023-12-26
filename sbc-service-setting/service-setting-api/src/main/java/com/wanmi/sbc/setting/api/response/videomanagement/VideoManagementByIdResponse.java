package com.wanmi.sbc.setting.api.response.videomanagement;

import com.wanmi.sbc.setting.bean.vo.VideoManagementVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>根据id查询任意（包含已删除）视频管理信息response</p>
 * @author zhaowei
 * @date 2021-04-17 17:47:22
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoManagementByIdResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 视频管理信息
     */
    @ApiModelProperty(value = "视频管理信息")
    private VideoManagementVO videoManagementVO;

}
