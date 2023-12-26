package com.wanmi.sbc.goods.log;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品审核时间
 * Created by daiyitian on 14/3/2017.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsCheckLog implements Serializable {

    /**
     * 订单号
     */
    @Id
    private String id;

    /**
     * 商品SpuId
     */
    private String goodsId;

    /**
     * 审核人
     */
    private String checker;

    /**
     * 审核时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime checkTime = LocalDateTime.now();

    /**
     * 审核状态
     */
    private CheckStatus auditStatus;

    /**
     * 审核原因
     */
    private String auditReason;

}
