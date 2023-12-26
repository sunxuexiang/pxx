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
 * @description: 通过id获取爆款时刻信息请求参数实体类
 * @author: XinJiang
 * @time: 2022/5/9 18:45
 */
@Data
@Builder
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class HotStyleMomentsGetByIdRequest implements Serializable {

    private static final long serialVersionUID = 4129739356723117111L;

    /**
     * 主键
     */
    @NotNull
    @ApiModelProperty(value = "主键id")
    private Long hotId;
}
