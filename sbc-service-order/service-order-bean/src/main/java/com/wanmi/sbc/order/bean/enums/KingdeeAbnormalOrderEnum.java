package com.wanmi.sbc.order.bean.enums;

import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * 金蝶订单异常类型
 *
 * @author yitang
 * @version 1.0
 */
@ApiEnum
public enum KingdeeAbnormalOrderEnum {
    @ApiEnumProperty("销售订单")
    SALESORDER(0),
    @ApiEnumProperty("支付订单")
    PAYORDER(1),
    @ApiEnumProperty("退货订单")
    RETURNGOODS(2),
    @ApiEnumProperty("退款订单")
    REFUNDORDER(3),
    @ApiEnumProperty("销售id")
    ORDERID(4),
    @ApiEnumProperty("洗囤货")
    WASHSTOCK(5),
    @ApiEnumProperty("洗囤货退货")
    WASHSTOCKRETURNS(6),
    @ApiEnumProperty("重推囤货")
    PUSHSTOCK(7),
    @ApiEnumProperty("重推囤货退货")
    PUSHSTOCKRETURNS(8),
    @ApiEnumProperty("洗退货囤货")
    WASHRETURNGOODSSTOCK(9),
    @ApiEnumProperty("重推新囤货订单")
    newPilePushStockOrder(10),
    @ApiEnumProperty("重推新囤货收款单")
    newPilePayCompensationOrder(11),
    @ApiEnumProperty("重推新囤货退款单")
    newPiLeCompensationRefundOrder(12),
    @ApiEnumProperty("建行佣金收款单重推")
    COMMISSIONPAYED(13),
    @ApiEnumProperty("建行佣金退款单重推")
    COMMISSIONREFUND(14),
    @ApiEnumProperty("囤货建行佣金退款单重推")
    NEWPILECOMMISSIONREFUND(15);

    private final Integer typeInt;

    KingdeeAbnormalOrderEnum(Integer typeInt){
        this.typeInt = typeInt;
    }

    public Integer toTypeInt(){
        return typeInt;
    }
}
