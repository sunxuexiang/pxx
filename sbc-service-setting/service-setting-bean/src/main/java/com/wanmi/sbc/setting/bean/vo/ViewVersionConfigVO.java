package com.wanmi.sbc.setting.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@ApiModel
@Data
public class ViewVersionConfigVO {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
     * 系统类型：android、ios
     */
    @ApiModelProperty(value = "systemType")
    private String systemType;

    /**
     * 版本号
     */
    @ApiModelProperty(value = "版本号")
    private String versionNo;

    /**
     * 更新提示状态：1-不提示更新，2-提示更新但不强制更新，3-强制更新
     */
    @ApiModelProperty(value = "更新提示状态：1-不提示更新，2-提示更新但不强制更新，3-强制更新")
    private Integer updatePromptStatus;

    /**
     * 下载地址
     */
    @ApiModelProperty(value = "下载地址")
    private String downLoadAddress;

    /**
     * 更新安装包大小（MB）
     */
    @ApiModelProperty(value = "更新安装包大小（MB）")
    private BigDecimal packageSize;

    /**
     * 更新描述
     */
    @ApiModelProperty(value = "更新描述")
    private String upgradeDesc;

    /**
     * 最新版本更新时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @ApiModelProperty(value = "最新版本更新时间")
    private LocalDateTime lastVersionUpdateTime;

}
