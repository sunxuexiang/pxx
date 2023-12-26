package com.wanmi.sbc.order.api.request.purchase;

import lombok.Data;

import java.util.List;

@Data
public class CheckPurchaseNumResponse {

    private List<CheckPurchaseNumVO> checkPurchaseNum;
}
