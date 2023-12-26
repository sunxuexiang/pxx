package com.wanmi.sbc.goods.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@ApiModel
@Data
public class GoodsBrandRecommendVO implements Serializable {

    /**
     * 品牌推荐id
     */
    @ApiModelProperty(value = "品牌推荐id")
    private Long goodsBrandRecommendId;

    /**
     * 品牌id
     */
    @ApiModelProperty(value = "品牌id")
    private Long brandId;

    /**
     * 品牌名称
     */
    @ApiModelProperty(value = "品牌名称")
    private String brandName;

    /**
     * 品牌logo
     */
    @ApiModelProperty(value = "品牌logo")
    private String logo;

    /**
     * 上下架的时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @ApiModelProperty(value = "上下架的时间")
    private LocalDateTime addedTime;


    /**
     * 上下架状态,0:下架1:上架
     */
    @ApiModelProperty(value = "上下架状态")
    private Integer addedFlag;


    /**
     * 删除标识,0:未删除1:已删除
     */
    @ApiModelProperty(value = "删除标识")
    private Integer delFlag;

    /**
     * 创建时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
}
