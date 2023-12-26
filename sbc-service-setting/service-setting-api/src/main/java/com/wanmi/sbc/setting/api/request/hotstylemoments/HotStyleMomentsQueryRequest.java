package com.wanmi.sbc.setting.api.request.hotstylemoments;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.setting.bean.enums.HotStyleMomentsStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @description: 查询爆款时刻请求参数实体类
 * @author: XinJiang
 * @time: 2022/5/9 18:46
 */
@Data
@Builder
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class HotStyleMomentsQueryRequest extends BaseQueryRequest {

    private static final long serialVersionUID = 2216497080652439602L;

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
     * 查询状态 0：全部，1：进行中，2：暂停中，3：未开始，4：已结束，5：进行中&未开始，6：终止
     */
    @ApiModelProperty(value = "查询状态 0：全部，1：进行中，2：暂停中，3：未开始，4：已结束，5：进行中&未开始，6：终止")
    private HotStyleMomentsStatus status;

    /**
     * 此刻
     */
    @ApiModelProperty(value = "当前时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime now = LocalDateTime.now();

}
