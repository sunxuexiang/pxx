package com.wanmi.sbc.shopcart.cart.ChainHandle;


import com.wanmi.sbc.shopcart.api.response.purchase.StockAndPureChainNodeRsponse;
import com.wanmi.sbc.shopcart.api.request.purchase.StockAndPureChainNodeRequeest;

/**
 * 库存检查区域限购检查营销限购检查
 */
public interface StockAndPureChainNode {

    StockAndPureChainNodeRsponse checkStockPure(StockAndPureChainNodeRequeest request);
}
