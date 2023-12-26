package com.wanmi.sbc.marketing.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.marketing.bean.enums.CoinActivityFullType;
import com.wanmi.sbc.marketing.bean.enums.CoinActivityType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * @author Administrator
 */
@ApiModel
@Data
public class CoinActivityVO implements Serializable {

    private static final long serialVersionUID = 512238496026684444L;

    /**
     * 优惠券活动id
     */
    @ApiModelProperty("活动ID")
    private String activityId;

    /**
     * 优惠券活动名称
     */
    @ApiModelProperty("活动名称")
    private String activityName;

    /**
     *  0：指定商品赠金币
     */
    @ApiModelProperty("活动类型")
    @Enumerated
    private CoinActivityType activityType;

    /**
     * 开始时间
     */
    @ApiModelProperty("开始时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty("结束时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime endTime;

    /**
     *
     * 0 任买返
     */
    @ApiModelProperty("任买返")
    @Enumerated
    private CoinActivityFullType coinActivityFullType;

    /**
     * 是否暂停 ，1 终止
     */
    @ApiModelProperty("是否暂停")
    @Enumerated
    private BoolFlag pauseFlag;

    /**
     * 商户id
     */
    @ApiModelProperty("商户id")
    private Long storeId;
    
    /**
     * 商家名称
     */
    @ApiModelProperty(value = "商家名称")
    private String supplierName;
    
    /**
     * 店铺名称
     */
    private String storeName;


    /**
     * 是否删除标志 0：否，1：是
     */
    @ApiModelProperty("是否删除标志")
    @Enumerated
    private DeleteFlag delFlag;

    /**
     * 关联的客户等级  -2指定客户 -1:全部客户 0:全部等级 other:其他等级 ,
     */
    @ApiModelProperty("关联的客户等级")
    private String joinLevel;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    private String createPerson;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

    /**
     * 修改人
     */
    @ApiModelProperty("修改人")
    private String updatePerson;

    /**
     * 删除时间
     */
    @ApiModelProperty("删除时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime delTime;

    /**
     * 删除人
     */
    @ApiModelProperty("删除人")
    private String delPerson;

    /**
     * 是否叠加（0：否，1：是）
     */
    @Enumerated
    @ApiModelProperty("是否叠加")
    private DefaultFlag isOverlap;

    /**
     * 仓库id
     */
    @ApiModelProperty("仓库id")
    private Long wareId;

    /**
     * 鲸币数量
     */
    @ApiModelProperty("鲸币数量")
    private BigDecimal coinNum;

    @Enumerated
    @ApiModelProperty("是否终止")
    private BoolFlag terminationFlag;

    /**
     * 真正的终止时间
     */
    @ApiModelProperty("真正的终止时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime realEndTime;

    private String goodsInfoId;
    
    /**
     * 订单返鲸币配置的商家数量
     */
    @ApiModelProperty("订单返鲸币配置的商家数量")
    private Integer storeCount;
}
