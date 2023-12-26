package com.wanmi.sbc.marketing.bean.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/5/30 15:32
 */
@Data
public class CoinActivityRecordDetailDto implements Serializable {

    private static final long serialVersionUID = -7516104352667666238L;

    private Long detailId;

    private Long recordId;

    private String activityId;

    private String orderNo;

    private BigDecimal coinNum;

    private String goodsInfoId;

    /**
     *  1：赠送，2：退回
     */
    private Integer recordType;

    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime recordTime;

    private DefaultFlag isOverlap = DefaultFlag.YES;


    private BigDecimal singleCoinNum;

    private Long goodsNum;
}
