package com.wanmi.sbc.setting.api.request.videomanagement;

import com.wanmi.sbc.setting.bean.enums.StateType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>描述<p>
 * 上下架状态调整
 *
 * @author zhaowei
 * @date 2021/4/19
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoManagementUpdateStateRequest implements Serializable {
    /**
     * ID
     */
    @ApiModelProperty(value = "ID")
    @NotNull
    private Long videoId;

    /**
     * 状态0:上架,1:下架
     */
    @ApiModelProperty(value = "状态0:上架,1:下架")
    @NotNull
    private StateType state;
}
