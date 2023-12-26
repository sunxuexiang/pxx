package com.wanmi.sbc.setting.api.request.popupadministration;


import com.wanmi.sbc.setting.api.request.SettingBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * <p>弹窗&页面管理</p>
 * @author weiwenhao
 * @date 2020-04-23
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageManagementRequest extends SettingBaseRequest {

    private static final long serialVersionUID = -516508803082218913L;

    /**
     * 应用页面
     */
    @ApiModelProperty(value = "应用页面")
    @NotNull
    private String applicationPageName;


    /**
     * 应用页面
     */
    @ApiModelProperty(value = "仓库id")
    private Long wareId;
}
