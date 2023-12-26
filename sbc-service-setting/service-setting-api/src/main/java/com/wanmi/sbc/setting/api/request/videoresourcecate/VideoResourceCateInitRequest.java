package com.wanmi.sbc.setting.api.request.videoresourcecate;

import com.wanmi.sbc.setting.api.request.SettingBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

/**
 * 视频教程资源资源分类表新增参数
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
public class VideoResourceCateInitRequest extends SettingBaseRequest {

    /**
     * 店铺标识
     */
    @ApiModelProperty(value = "店铺标识")
    @NotNull
    private Long storeId;

    /**
     * 商家标识
     */
    @ApiModelProperty(value = "商家标识")
    @NotNull
    private Long companyInfoId;

    /**
     * 父分类ID
     */
    @ApiModelProperty(value = "父分类ID")
    @Length(max = 36)
    private String cateParentId;
}