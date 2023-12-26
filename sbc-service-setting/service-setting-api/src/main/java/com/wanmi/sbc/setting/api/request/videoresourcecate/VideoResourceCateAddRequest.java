package com.wanmi.sbc.setting.api.request.videoresourcecate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.setting.api.request.SettingBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 视频教程资源分类表新增
 *
 * @author hudong
 * @date 2023-06-23 16:13:19
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoResourceCateAddRequest extends SettingBaseRequest {
    private static final long serialVersionUID = 1L;

    /**
     * 店铺标识
     */
    @ApiModelProperty(value = "店铺标识")
    private Long storeId;

    /**
     * 商家标识
     */
    @ApiModelProperty(value = "商家标识")
    private Long companyInfoId;

    /**
     * 分类名称
     */
    @ApiModelProperty(value = "分类名称")
    @Length(max = 45)
    @NotNull
    private String cateName;

    /**
     * 父分类ID
     */
    @ApiModelProperty(value = "父分类ID")
    @Length(max = 36)
    private String cateParentId;

    /**
     * 分类图片
     */
    @ApiModelProperty(value = "分类图片")
    @Length(max = 255)
    private String cateImg;

    /**
     * 分类层次路径,例1|01|001
     */
    @ApiModelProperty(value = "分类层次路径,例1|01|001")
    @Length(max = 1000)
    private String catePath;

    /**
     * 分类层级
     */
    @ApiModelProperty(value = "分类层级")
    @Max(127)
    private Integer cateGrade;

    /**
     * 拼音
     */
    @ApiModelProperty(value = "拼音")
    @Length(max = 45)
    private String pinYin;

    /**
     * 简拼
     */
    @ApiModelProperty(value = "简拼")
    @Length(max = 45)
    private String spinYin;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

    /**
     * 删除标识,0:未删除1:已删除
     */
    @ApiModelProperty(value = "删除标识,0:未删除1:已删除")
    private DeleteFlag delFlag;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    @Max(127)
    private Integer sort;

    /**
     * 是否默认,0:否1:是
     */
    @ApiModelProperty(value = "是否默认,0:否1:是")
    private DefaultFlag isDefault;

    /**
     * 分类类别 1-商家操作视频分类 2-用户操作视频分类
     */
    @ApiModelProperty(value = "分类类别")
    private Integer cateType;

}