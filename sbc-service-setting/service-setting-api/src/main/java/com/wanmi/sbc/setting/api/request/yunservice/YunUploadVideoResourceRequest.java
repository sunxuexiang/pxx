package com.wanmi.sbc.setting.api.request.yunservice;

import com.wanmi.sbc.setting.api.request.SettingBaseRequest;
import com.wanmi.sbc.setting.bean.enums.ResourceType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>云上传文件参数</p>
 *
 * @author hudong
 * @date 2023-07-01 18:33:04
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YunUploadVideoResourceRequest extends SettingBaseRequest {
    private static final long serialVersionUID = 1L;

    /**
     * 上传素材分类id
     */
    @ApiModelProperty(value = "上传素材分类id")
    private String cateId;

    /**
     * 店铺id
     */
    @ApiModelProperty(value = "店铺id")
    private Long storeId;

    /**
     * 商家id
     */
    @ApiModelProperty(value = "商家id")
    private Long companyInfoId;

    /**
     * 素材类型
     */
    @ApiModelProperty(value = "素材类型")
    private ResourceType resourceType;

    /**
     * 素材地址
     */
    @ApiModelProperty(value = "素材地址")
    private String artworkUrl;

    /**
     * 素材KEY
     */
    @ApiModelProperty(value = "素材KEY")
    private String resourceKey;

    /**
     * 素材名称
     */
    @ApiModelProperty(value = "素材名称")
    private String resourceName;

    /**
     * 素材内容
     */
    @ApiModelProperty(value = "素材内容")
    private byte[] content;

    /**
     * 横竖屏类型
     */
    @ApiModelProperty(value = "横竖屏类型")
    private Integer hvType;

}