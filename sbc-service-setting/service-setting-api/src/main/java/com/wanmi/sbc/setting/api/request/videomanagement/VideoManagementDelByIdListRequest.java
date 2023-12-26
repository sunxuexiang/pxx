package com.wanmi.sbc.setting.api.request.videomanagement;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;
import java.util.List;

/**
 * <p>批量删除视频管理请求参数</p>
 *
 * @author zhaowei
 * @date 2021-04-17 17:47:22
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoManagementDelByIdListRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 批量删除-IDList
     */
    @ApiModelProperty(value = "批量删除-IDList")
    @NotEmpty
    private List<Long> videoIdList;
}
