package com.wanmi.sbc.setting.api.request.videomanagement;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.setting.bean.enums.StateType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * <p>视频管理新增参数</p>
 *
 * @author zhaowei
 * @date 2021-04-17 17:47:22
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoManagementAddRequest implements Serializable {
    private static final long serialVersionUID = 1L;

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
    @Length(max = 255)
    private String artworkUrl;

    /**
     * 删除标识,0:未删除1:已删除
     */
    @ApiModelProperty(value = "删除标识,0:未删除1:已删除", hidden = true)
    private DeleteFlag delFlag;

    /**
     * oss服务器类型，对应system_config的config_type
     */
    @ApiModelProperty(value = "oss服务器类型，对应system_config的config_type")
    private String serverType;

    /**
     * 上传用户id
     */
    @ApiModelProperty(value = "上传用户id")
    private String coverFollowCustomerId;

    /**
     * 封面地址
     */
    @ApiModelProperty(value = "封面地址")
    private String coverImg;

    /**
     * 店铺ID
     */
    @ApiModelProperty(value = "店铺ID")
    private Long storeId;

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