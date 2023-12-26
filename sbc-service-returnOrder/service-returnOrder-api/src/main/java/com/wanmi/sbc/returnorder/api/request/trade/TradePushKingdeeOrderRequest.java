package com.wanmi.sbc.returnorder.api.request.trade;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.returnorder.bean.enums.KingdeeAbnormalOrderEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.Convert;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 补偿push金蝶失败单子
 *
 * @author yitang
 * @version 1.0
 */
@Setter
@Getter
public class TradePushKingdeeOrderRequest implements Serializable {

    private static final long serialVersionUID = 5161990104859975500L;

    /**
     * 订单类型
     */
    private KingdeeAbnormalOrderEnum orderType;

    /**
     * 截止时间(天)
     */
    private Integer day;

    /**
     * 以时间为准
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime dateTime;

    /**
     * 结束时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime endTime;

    /**
     * 销售订单
     */
    private String orderIds;

}
