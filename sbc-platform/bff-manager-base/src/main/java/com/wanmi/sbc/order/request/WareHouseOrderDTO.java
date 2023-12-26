package com.wanmi.sbc.order.request;

import lombok.Data;

/**
 * @author baijianzhong
 * @ClassName WareHouseOrderDTO
 * @Date 2020-09-22 00:44
 * @Description TODO
 **/
@Data
public class WareHouseOrderDTO {

    /**
     * 订单的Id
     */
    private Integer orderIndex;

    /**
     * 分仓的Id
     */
    private Long wareId;
}
