package com.wanmi.sbc.returnorder.shopcart.ChainHandle;


import com.wanmi.sbc.returnorder.api.request.purchase.StockAndPureChainNodeRequeest;
import com.wanmi.sbc.returnorder.api.response.purchase.StockAndPureChainNodeRsponse;


/**
 * 库存检查区域限购检查营销限购检查
 */
public interface StockAndPureChainNode {

    StockAndPureChainNodeRsponse checkStockPure(StockAndPureChainNodeRequeest request);
}
