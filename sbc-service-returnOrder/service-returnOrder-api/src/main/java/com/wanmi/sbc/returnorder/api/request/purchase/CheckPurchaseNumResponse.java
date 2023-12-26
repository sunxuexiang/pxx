package com.wanmi.sbc.returnorder.api.request.purchase;

import lombok.Data;

import java.util.List;

@Data
public class CheckPurchaseNumResponse {

    private List<CheckPurchaseNumVO> checkPurchaseNum;
}
