package com.wanmi.sbc.setting.hotstylemoments.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.setting.hotstylemoments.model.root.HotStyleMoments;
import com.wanmi.sbc.setting.hotstylemoments.model.root.HotStyleMomentsConfig;
import com.wanmi.sbc.setting.hotstylemoments.model.validgroups.NotHotId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description: 爆款时刻信息保存请求参数实体类
 * @author: XinJiang
 * @time: 2022/5/9 18:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotStyleMomentsSaveRequest implements Serializable {

    private static final long serialVersionUID = -4444420556528617828L;

    /**
     * 主键
     */
    @NotNull(groups = NotHotId.class)
    private Long hotId;

    /**
     * 爆款时刻主题名称
     */
    @NotBlank
    @Length(max = 100)
    private String hotName;

    /**
     * 开始时间
     */
    @NotNull
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime beginTime;

    /**
     * 结束时间
     */
    @NotNull
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime endTime;

    /**
     * 详情页banner图标地址
     */
    @NotBlank
    private String bannerImageUrl;

    /**
     * 是否暂停（1：暂停，0：正常）
     */
    @NotNull
    private DefaultFlag isPause;

    /**
     * 终止标志位：1：终止，0非终止
     */
    @NotNull
    private DefaultFlag terminationFlag;

    /**
     * 删除标记  0：正常，1：删除
     */
    @NotNull
    private DeleteFlag delFlag;

    /**
     * 创建时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    private String createPerson;

    /**
     * 更新时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

    /**
     * 更新人
     */
    private String updatePerson;

    /**
     * 删除时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime deleteTime;

    /**
     * 删除人
     */
    private String deletePerson;

    /**
     * 商品配置信息列表
     */
    @Size(max = 50)
    private List<HotStyleMomentsConfig> hotStyleMomentsConfigs;

    public HotStyleMoments generateHotStyleMoments() {
        HotStyleMoments hotStyleMoments = new HotStyleMoments();
        hotStyleMoments.setHotName(hotName);
        hotStyleMoments.setBeginTime(beginTime);
        hotStyleMoments.setEndTime(endTime);
        hotStyleMoments.setBannerImageUrl(bannerImageUrl);
        hotStyleMoments.setIsPause(isPause);
        hotStyleMoments.setTerminationFlag(terminationFlag);
        hotStyleMoments.setDelFlag(delFlag);

        hotStyleMoments.setCreateTime(createTime);
        hotStyleMoments.setCreatePerson(createPerson);
        return hotStyleMoments;
    }

    public List<HotStyleMomentsConfig> generateHotStyleMomentsConfigs(Long hotId){
        hotStyleMomentsConfigs.forEach(config -> config.setHotId(hotId));
        return hotStyleMomentsConfigs;
    }
}
