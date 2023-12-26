package com.wanmi.sbc.marketing.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import java.time.LocalDateTime;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/5/22 16:10
 */
@ApiModel
@Data
public class CoinActivityGoodsVo {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 活动id
     */
    private String activityId;

    /**
     * 商品信息skuid
     */
    private String goodsInfoId;

    /**
     * 是否终止
     */
    private BoolFlag terminationFlag;

    /**
     * 终止时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime terminationTime;

    /**
     * 是否显示
     * 0 显示 1 不显示
     */
    private Integer displayType;
}
