package com.wanmi.sbc.setting.api.request.videomanagement;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.setting.bean.enums.StateType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>视频管理修改参数</p>
 *
 * @author zhaowei
 * @date 2021-04-17 17:47:22
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoManagementModifyRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ApiModelProperty(value = "ID")
    @Max(9223372036854775807L)
    private Long videoId;

    /**
     * 视频名称
     */
    @ApiModelProperty(value = "视频名称")
    @Length(max = 50)
    private String videoName;

    /**
     * 状态0:上架,1:下架
     */
    @ApiModelProperty(value = "状态0:上架,1:下架")
    private StateType state;

    /**
     * 素材KEY
     */
    @ApiModelProperty(value = "素材KEY")
    @Length(max = 255)
    private String resourceKey;

    /**
     * 素材地址
     */
    @ApiModelProperty(value = "素材地址")
    @Length(max = 255)
    private String artworkUrl;

    /**
     * 发布时间
     */
    @ApiModelProperty(value = "发布时间", hidden = true)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * oss服务器类型，对应system_config的config_type
     */
    @ApiModelProperty(value = "oss服务器类型，对应system_config的config_type")
    private String serverType;

    /**
     * 封面地址
     */
    @ApiModelProperty(value = "封面地址")
    private String coverImg;

    /**
     * 商品链接
     */
    @ApiModelProperty(value = "商品链接")
    private String goodsLink;

    /**
     * 商品skuId
     */
    @ApiModelProperty(value = "商品skuId")
    private String goodsInfoId;

    /**
     * 商品Id
     */
    @ApiModelProperty(value = "商品Id")
    private String goodsId;

}