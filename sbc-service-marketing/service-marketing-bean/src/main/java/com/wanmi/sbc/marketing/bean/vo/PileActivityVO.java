package com.wanmi.sbc.marketing.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.marketing.bean.enums.PileActivityType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>囤货活动</p>
 * author: chenchang
 * Date: 2022-09-06
 */
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PileActivityVO implements Serializable {

    private static final long serialVersionUID = -1876493701433382583L;

    @ApiModelProperty(value = "囤货活动id")
    private String activityId;

    @ApiModelProperty(value = "囤货活动名称")
    private String activityName;

    @ApiModelProperty(value = "开始时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime startTime;

    @ApiModelProperty(value = "结束时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime endTime;

    @ApiModelProperty(value = "囤货类型: 1 全款囤货")
    private PileActivityType pileActivityType;

    @ApiModelProperty(value = "公共虚拟库存")
    private Long publicVirtualStock;

    @ApiModelProperty(value = "商户id")
    private Long storeId;

    @ApiModelProperty(value = "是否已删除")
    @Enumerated
    private DeleteFlag delFlag;

    @ApiModelProperty(value = "是否终止")
    @Enumerated
    private BoolFlag terminationFlag;

    @ApiModelProperty(value = "创建时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "创建人")
    private String createPerson;

    @ApiModelProperty(value = "修改时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "修改人")
    private String updatePerson;

    @ApiModelProperty(value = "删除时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime delTime;

    @ApiModelProperty(value = "删除人")
    private String delPerson;

    @ApiModelProperty(value = "是否强制囤货")
    @Enumerated
    private BoolFlag forcePileFlag;

    @ApiModelProperty(value = "店铺名称")
    private String storeName;
}
