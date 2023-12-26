package com.wanmi.sbc.marketing.bean.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;

import lombok.Data;


@Data
public class CoinActivityStoreRecordDetailDTO implements Serializable {

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



    private BigDecimal singleCoinNum;

    private Long goodsNum;
    
	/**
	 * 店铺id
	 */
	private Long storeId;
	
	/**
	 * 店铺name
	 */
	private String storeName;
}
