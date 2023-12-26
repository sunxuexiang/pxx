package com.wanmi.sbc.order.api.request.trade;

import com.wanmi.sbc.goods.bean.enums.SaleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/5/31 9:02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CoinActivityPushKingdeeRequest implements Serializable {

    private String tid;

    private String buyerAccount;

    private BigDecimal applyPrice;

    private SaleType saleType;

    private String sendNo;

    private String pushDateStr;
}
