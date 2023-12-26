package com.wanmi.sbc.marketing.api.request.pile;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PileActivityStockRequest implements Serializable {

    /**
     *
     */
    private String goodsInfoId;

    /**
     * 库存变更数量
      */
    private Integer num;

    /**
     * 库存变更数量
     */
    private String activityId;

    /**
     * true扣减 fasle 增加
     */
    private Boolean addOrSub;
}
