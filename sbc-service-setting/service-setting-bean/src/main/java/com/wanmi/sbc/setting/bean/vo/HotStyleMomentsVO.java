package com.wanmi.sbc.setting.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.setting.bean.enums.HotStyleMomentsStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description: 爆款时刻信息VO实体类
 * @author: XinJiang
 * @time: 2022/5/9 18:19
 */
@Data
@ApiModel
public class HotStyleMomentsVO implements Serializable {


    private static final long serialVersionUID = 8690059454525899438L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long hotId;

    /**
     * 爆款时刻主题名称
     */
    @ApiModelProperty(value = "爆款时刻主题名称")
    private String hotName;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime beginTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime endTime;

    /**
     * 详情页banner图标地址
     */
    @ApiModelProperty(value = "详情页banner图标地址")
    private String bannerImageUrl;

    /**
     * 是否暂停（1：暂停，0：正常）
     */
    @ApiModelProperty(value = "是否暂停（1：暂停，0：正常）")
    private DefaultFlag isPause;

    /**
     * 终止标志位：1：终止，0非终止
     */
    @ApiModelProperty(value = "终止标志位：1：终止，0非终止")
    private DefaultFlag terminationFlag;

    /**
     * 删除标记  0：正常，1：删除
     */
    @ApiModelProperty(value = "删除标记  0：正常，1：删除")
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
    private LocalDateTime deleteTime;

    /**
     * 删除人
     */
    @ApiModelProperty(value = "删除人")
    private String deletePerson;

    /**
     * 商品配置信息列表
     */
    @ApiModelProperty(value = "商品配置信息列表")
    private List<HotStyleMomentsConfigVO> hotStyleMomentsConfigs;

    /**
     * 状态
     */
    @ApiModelProperty(value = "活动状态")
    private HotStyleMomentsStatus status;

    /**
     * 获取活动状态
     * @return
     */
    public HotStyleMomentsStatus getStatus(){
        if(beginTime != null && endTime != null){
            if(LocalDateTime.now().isBefore(beginTime)){
                return HotStyleMomentsStatus.NOT_START;
            }else if(LocalDateTime.now().isAfter(endTime)){
                return HotStyleMomentsStatus.ENDED;
            }else if(isPause.equals(DefaultFlag.YES)){
                return HotStyleMomentsStatus.PAUSED;
            }else if(terminationFlag.equals(DefaultFlag.YES)){
                return HotStyleMomentsStatus.TERMINATION;
            }else{
                return HotStyleMomentsStatus.STARTED;
            }
        }
        return null;
    }
}
