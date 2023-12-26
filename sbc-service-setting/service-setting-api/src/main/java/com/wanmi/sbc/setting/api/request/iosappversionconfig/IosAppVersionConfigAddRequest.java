package com.wanmi.sbc.setting.api.request.iosappversionconfig;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.setting.api.request.SettingBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;

/**
 * <p>平台素材资源分类新增参数</p>
 *
 * @author lq
 * @date 2019-11-05 16:14:55
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IosAppVersionConfigAddRequest extends SettingBaseRequest {
    private static final long serialVersionUID = 6545645647489789411L;
    /**
     * 版本号
     */
    @ApiModelProperty(value = "版本号")
    private String versionNo;

    /**
     * 版本构建号
     */
    @ApiModelProperty(value = "版本构建号")
    private Long buildNo;

    /**
     * 更新提示状态：1-不提示更新，2-提示更新但不强制更新，3-强制更新
     */
    @ApiModelProperty(value = "更新提示状态：1-不提示更新，2-提示更新但不强制更新，3-强制更新")
    private Integer updatePromptStatus;

    /**
     * 是否打开微信登录标志：0-否，1-是
     */
    @ApiModelProperty(value = "是否打开微信登录标志：0-否，1-是")
    private Integer openWechatLongFlag;

    /**
     * 最新版本更新时间
     */
    @ApiModelProperty(value = "最新版本更新时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime lastVersionUpdateTime;
}
