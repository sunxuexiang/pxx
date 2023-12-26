package com.wanmi.sbc.setting.api.request.videomanagement;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.setting.bean.enums.StateType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>视频管理列表查询请求参数</p>
 *
 * @author zhaowei
 * @date 2021-04-17 17:47:22
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoManagementListRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 批量查询-IDList
     */
    @ApiModelProperty(value = "批量查询-IDList")
    private List<Long> videoIdList;

    /**
     * ID
     */
    @ApiModelProperty(value = "ID")
    private Long videoId;

    /**
     * 视频名称
     */
    @ApiModelProperty(value = "视频名称")
    private String videoName;

    /**
     * 状态0:上架,1:下架
     */
    @ApiModelProperty(value = "状态0:上架,1:下架")
    private StateType state;

    /**
     * 播放数
     */
    @ApiModelProperty(value = "播放数")
    private Long playFew;

    /**
     * 素材KEY
     */
    @ApiModelProperty(value = "素材KEY")
    private String resourceKey;

    /**
     * 素材地址
     */
    @ApiModelProperty(value = "素材地址")
    private String artworkUrl;

    /**
     * 搜索条件:发布时间开始
     */
    @ApiModelProperty(value = "搜索条件:发布时间开始")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTimeBegin;
    /**
     * 搜索条件:发布时间截止
     */
    @ApiModelProperty(value = "搜索条件:发布时间截止")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTimeEnd;

    /**
     * 搜索条件:更新时间开始
     */
    @ApiModelProperty(value = "搜索条件:更新时间开始")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTimeBegin;
    /**
     * 搜索条件:更新时间截止
     */
    @ApiModelProperty(value = "搜索条件:更新时间截止")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTimeEnd;

    /**
     * 删除标识,0:未删除1:已删除
     */
    @ApiModelProperty(value = "删除标识,0:未删除1:已删除")
    private DeleteFlag delFlag;

    /**
     * oss服务器类型，对应system_config的config_type
     */
    @ApiModelProperty(value = "oss服务器类型，对应system_config的config_type")
    private String serverType;

}