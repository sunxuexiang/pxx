//package com.wanmi.sbc.order.trade.reponse;
//
//import com.alibaba.fastjson.JSON;
//import lombok.Data;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.search.aggregations.bucket.terms.Terms;
//
///**
// * Created by sunkun on 2017/9/18.
// */
//@Data
//public class TradeTodoReponse {
//
//    /**
//     * 待审核
//     */
//    private long waitAudit;
//
//    /**
//     * 待付款
//     */
//    private long waitPay = 0;
//
//    /**
//     * 待发货
//     */
//    private long waitDeliver;
//
//    /**
//     * 待收货
//     */
//    private long waitReceiving;
//
//
//    /**
//     * 待审核订单 true:开启 false:关闭
//     */
//    private boolean tradeCheckFlag = false;
//
//    private TradeTodoReponse returnTradeTodo(SearchResponse searchResponse) {
//        System.err.println("searchResponse--->" + searchResponse);
//        ((Terms) (searchResponse).getAggregations().getAsMap().get("orderType")).getBuckets().stream().forEach(info -> {
//            Long count = info.getDocCount();
//            System.err.println("count--->" + info.getDocCount());
//            System.err.println("key==->" + info.getKey());
//            ((Terms) info.getAggregations().getAsMap().get("payState")).getBuckets().stream().forEach(infos -> {
//                if (infos.getKey().equals("not_paid")) {
//                    this.waitPay += infos.getDocCount();
//                }
//            });
//
//            switch (info.getKey().toString()) {
//                case "init":
//                    this.waitAudit = count;
//                    break;
//                case "audit":
//                    this.waitDeliver += count;
//                    break;
//                case "delivered":
//                    this.waitReceiving = count;
//                    break;
//                case "delivered_part":
//                    this.waitDeliver += count;
//                    break;
//            }
////            System.err.println(info.getKeyAsString());
////            System.err.println(((InternalSum) (info.getAggregations().getAsMap().get("orderAmtSum"))).getValue());
////            System.err.println(info.getDocCount());
//        });
//        System.err.println("json--->"+JSON.toJSONString(this));
//        System.err.println("payCount--->" + this.waitPay);
//        return this;
//    }
//
//    public static TradeTodoReponse build(SearchResponse searchResponse) {
////        System.err.println("searchResponse--->" + searchResponse);
////        ((Terms) (searchResponse).getAggregations().getAsMap().get("orderType")).getBuckets().stream().forEach(info -> {
////            info.getDocCount();
////            System.err.println("count--->"+info.getDocCount());
////            System.err.println("key==->"+info.getKey());
////            switch (info.getKey()){
////                case "init":
////
////            }
//////            System.err.println(info.getKeyAsString());
//////            System.err.println(((InternalSum) (info.getAggregations().getAsMap().get("orderAmtSum"))).getValue());
//////            System.err.println(info.getDocCount());
////        });
//        return new TradeTodoReponse().returnTradeTodo(searchResponse);
//    }
//}
