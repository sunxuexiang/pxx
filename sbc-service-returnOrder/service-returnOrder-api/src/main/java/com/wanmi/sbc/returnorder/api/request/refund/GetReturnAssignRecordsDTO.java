package com.wanmi.sbc.returnorder.api.request.refund;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel
public class GetReturnAssignRecordsDTO implements Serializable {
    private Long id;
    /**
     * 退货单号
     */
    private String returnOrderNo;

    /**
     * 提货单号
     */
    private String pickOrderNo;

    /**
     * 囤货单编号
     */
    private String newPileOrderNo;

    /**
     * 仓库ID
     */
    private Long wareId;

    /**
     * skuId
     */
    private String goodsInfoId;

    /**
     * 提货数量
     */
    private Long num;
}
