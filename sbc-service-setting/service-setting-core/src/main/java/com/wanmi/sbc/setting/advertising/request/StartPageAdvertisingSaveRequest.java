package com.wanmi.sbc.setting.advertising.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateSerializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.setting.advertising.model.root.StartPageAdvertising;
import com.wanmi.sbc.setting.advertising.model.validgroups.NotAdvertisingId;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @description: 启动页广告信息保存请求类
 * @author: XinJiang
 * @time: 2022/3/31 11:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartPageAdvertisingSaveRequest implements Serializable {

    private static final long serialVersionUID = -6130788779762160975L;

    /**
     * id
     */
    @NotNull(groups = {NotAdvertisingId.class})
    private String advertisingId;

    /**
     * 广告名称
     */
    @ApiModelProperty(value = "广告名称")
    private String advertisingName;

    /**
     * 背景颜色
     */
    @ApiModelProperty(value = "背景颜色")
    private String backgroundColor;

    /**
     * 图片地址
     */
    @ApiModelProperty(value = "图片地址")
    private String imageUrl;

    /**
     * 是否设置跳转链接，0-否，1-是
     */
    @ApiModelProperty(value = "是否设置跳转链接，0-否，1-是")
    private DefaultFlag linkFlag = DefaultFlag.NO;

    /**
     * 魔方海报页名称
     */
    @ApiModelProperty(value = "魔方海报页名称")
    private String mofangName;

    /**
     * 魔方海报页编号
     */
    @ApiModelProperty(value = "魔方海报页编号")
    private String mofangCode;

    /**
     * 生效类型 0：立即生效，1：固定时间
     */
    @ApiModelProperty(value = "生效类型 0：立即生效，1：固定时间")
    private DefaultFlag effectType = DefaultFlag.NO;

    /**
     * 状态，0关闭 1开启
     */
    @ApiModelProperty(value = "状态，0关闭 1开启")
    private DefaultFlag status;

    /**
     * 生效时间
     */
    @ApiModelProperty(value = "生效时间")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate effectDate;

    /**
     * 是否删除标志 0：否，1：是
     */
    @NotNull
    @ApiModelProperty(value = "是否删除标志 0：否，1：是")
    private DeleteFlag delFlag;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createPerson;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    private String updatePerson;

    /**
     * 删除时间
     */
    @ApiModelProperty(value = "删除时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime delTime;

    /**
     * 删除人
     */
    @ApiModelProperty(value = "删除人")
    private String delPerson;

    public StartPageAdvertising generateStartPageAdvertising(){
        StartPageAdvertising startPageAdvertising = new StartPageAdvertising();
        startPageAdvertising.setAdvertisingName(advertisingName);
        startPageAdvertising.setBackgroundColor(backgroundColor);
        startPageAdvertising.setImageUrl(imageUrl);
        startPageAdvertising.setLinkFlag(linkFlag);
        startPageAdvertising.setMofangName(mofangName);
        startPageAdvertising.setMofangCode(mofangCode);
        startPageAdvertising.setEffectType(effectType);
        startPageAdvertising.setEffectDate(effectDate);
        //默认关闭 需要手动开启
        startPageAdvertising.setStatus(DefaultFlag.NO);
        if (Objects.nonNull(delFlag)) {
            startPageAdvertising.setDelFlag(delFlag);
        } else {
            startPageAdvertising.setDelFlag(DeleteFlag.NO);
        }
        startPageAdvertising.setCreatePerson(createPerson);
        startPageAdvertising.setCreateTime(createTime);
        return startPageAdvertising;
    }
}
