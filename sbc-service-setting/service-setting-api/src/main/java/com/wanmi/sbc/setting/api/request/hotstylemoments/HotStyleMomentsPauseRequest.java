package com.wanmi.sbc.setting.api.request.hotstylemoments;

import com.wanmi.sbc.common.enums.DefaultFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @description: 启动或暂停爆款时刻请求参数类
 * @author: XinJiang
 * @time: 2022/5/9 20:36
 */
@Data
@Builder
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class HotStyleMomentsPauseRequest implements Serializable {

    private static final long serialVersionUID = 5847791964397212476L;

    /**
     * 主键
     */
    @NotNull
    @ApiModelProperty(value = "主键id")
    private Long hotId;

    /**
     * 是否暂停（1：暂停，0：正常）
     */
    @NotNull
    @ApiModelProperty(value = "是否暂停（1：暂停，0：正常）")
    private DefaultFlag isPause;
}
