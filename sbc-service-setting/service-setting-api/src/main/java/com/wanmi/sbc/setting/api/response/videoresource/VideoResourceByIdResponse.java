package com.wanmi.sbc.setting.api.response.videoresource;

import com.wanmi.sbc.setting.bean.vo.VideoResourceVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 根据id查询任意（包含已删除）视频教程资源库信息response</p>
 * @author hudong
 * @date 2023-06-26 16:12:49
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoResourceByIdResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 店铺资源库信息
     */
    @ApiModelProperty(value = "视频教程资源库信息")
    private VideoResourceVO videoResourceVO;
}
