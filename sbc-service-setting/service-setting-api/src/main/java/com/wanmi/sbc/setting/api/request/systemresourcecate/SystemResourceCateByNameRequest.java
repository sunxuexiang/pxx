package com.wanmi.sbc.setting.api.request.systemresourcecate;

import com.wanmi.sbc.setting.api.request.SettingBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 *
 *
 *@description: 单个查询平台素材资源分类请求参数
 *@author: XinJiang
 *@time: 2022/2/6 16:48
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemResourceCateByNameRequest extends SettingBaseRequest {

    private static final long serialVersionUID = 7531268734047621499L;

    /**
     * 素材资源分类id
     */
    @ApiModelProperty(value = "素材资源分类名称")
    @NotNull
    private String cateName;
}
