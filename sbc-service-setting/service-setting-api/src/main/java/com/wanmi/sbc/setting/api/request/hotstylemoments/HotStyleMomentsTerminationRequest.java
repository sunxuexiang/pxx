package com.wanmi.sbc.setting.api.request.hotstylemoments;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *@description: 终止活动请求参数实体类了
 *@author: XinJiang
 *@time: 2022/5/9 20:33
 */
@Data
@Builder
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class HotStyleMomentsTerminationRequest implements Serializable {

    private static final long serialVersionUID = 7283869012639705874L;

    /**
     * 主键
     */
    @NotNull
    @ApiModelProperty(value = "主键id")
    private Long hotId;
}
