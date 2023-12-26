package com.wanmi.sbc.setting.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.setting.bean.enums.AdvertisingType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description: 首页广告位VO实体类
 * @author: XinJiang
 * @time: 2022/2/18 10:02
 */
@ApiModel
@Data
public class AdvertisingVO implements Serializable {

    /**
     * 首页广告位ID
     */
    @ApiModelProperty(value = "首页广告位ID")
    private String advertisingId;

    /**
     * 广告名称
     */
    @ApiModelProperty(value = "广告名称")
    private String advertisingName;

    /**
     * 广告位类型：0-通栏推荐位，1-分栏推荐位，2-轮播推荐位
     */
    @ApiModelProperty(value = "广告位类型：0-通栏推荐位，1-分栏推荐位，2-轮播推荐位")
    private AdvertisingType advertisingType;

    /**
     * 是否删除标志 0：否，1：是
     */
    @ApiModelProperty(value = "是否删除标志 0：否，1：是")
    private DeleteFlag delFlag;

    /**
     * 排序顺序
     */
    @ApiModelProperty(value = "排序顺序")
    private Integer sortNum;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建日期")
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
    private LocalDateTime delTime;

    /**
     * 删除人
     */
    @ApiModelProperty(value = "删除人")
    private String delPerson;

    /**
     * 仓库id
     */
    @ApiModelProperty(value = "仓库id")
    private Long wareId;

    /**
     * 店铺id
     */
    @ApiModelProperty(value = "店铺id")
    private Long storeId;

    /**
     * 店铺名称
     */
    @ApiModelProperty(value = "店铺名称")
    private String storeName;

    /**
     * 广告位配置信息
     */
    @ApiModelProperty(value = "广告位配置信息")
    private List<AdvertisingConfigVO> advertisingConfigList;
}
