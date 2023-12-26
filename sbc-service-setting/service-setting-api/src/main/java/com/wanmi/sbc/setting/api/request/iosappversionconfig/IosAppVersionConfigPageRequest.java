package com.wanmi.sbc.setting.api.request.iosappversionconfig;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @Description: ios版本配置信息分页请求类
 * @author: jiangxin
 * @create: 2021-09-23 15:44
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IosAppVersionConfigPageRequest extends BaseQueryRequest {

    private static final long serialVersionUID = -5415035971937038999L;

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
