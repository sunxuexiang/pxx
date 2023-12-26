package com.wanmi.sbc.marketing.bean.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/5/30 15:27
 */
@Data
public class CoinActivityRecordDto implements Serializable {

    private static final long serialVersionUID = -1774942254914730894L;

    private Long recordId;

    private String sendNo;


    private String activityId;


    private String customerAccount;

    private String orderNo;

    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime orderTime;

    private BigDecimal orderPrice;

    private BigDecimal coinNum;

    /**
     *  1：赠送，2：退回
     */
    private Integer recordType;

    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime recordTime;

    List<CoinActivityRecordDetailDto> detailList;
}
