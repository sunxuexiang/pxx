package com.wanmi.sbc.wms.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 描述
 *
 * @author yitang
 * @version 1.0
 */
@Setter
@Getter
public class DescriptionFailedQueryStockPushReturnGoodsVO implements Serializable {

    /**
     * id
     */
    private Long pushKingdeeReturnGoodsId;

    /**
     * 囤货退货编号
     */
    private String stockReturnGoodsCode;

    /**
     * 推送给金蝶状态，0：创建 1：推送成功 2推送失败
     */
    private Integer pushStatus;

    /**
     * 说明
     */
    private String instructions;

    /**
     * 囤货订单号
     */
    private String stockOrderCode;

    /**
     * 创建时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;
}
