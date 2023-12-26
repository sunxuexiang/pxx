package com.wanmi.sbc.order;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.order.bean.dto.PickGoodsDTO;
import com.wanmi.sbc.order.bean.enums.ReturnReason;
import com.wanmi.sbc.order.bean.enums.ReturnWay;
import com.wanmi.sbc.order.bean.enums.TradeActivityTypeEnum;
import com.wanmi.sbc.order.returnorder.model.entity.ReturnItem;
import com.wanmi.sbc.order.returnorder.model.root.ReturnOrder;
import com.wanmi.sbc.order.returnorder.model.value.ReturnPrice;
import com.wanmi.sbc.order.returnorder.service.ReturnOrderService;
import org.apache.commons.lang3.builder.DiffResult;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinwei on 27/4/2017.
 */
public class ReturnOrderTest {

    @Test
    public void diffOneTest() {

        ReturnOrder oldReturn = new ReturnOrder();
        ReturnOrder newReturn = new ReturnOrder();

        oldReturn.setReturnReason(ReturnReason.BADGOODS);
        newReturn.setReturnReason(ReturnReason.ERRORGOODS);

        DiffResult diff = oldReturn.diff(newReturn);
        ReturnReason left = (ReturnReason) (diff.getDiffs().get(0).getLeft());
        ReturnReason right = (ReturnReason) (diff.getDiffs().get(0).getRight());
        System.out.println(String.format("退货原因由 %s 改为 %s", left.getDesc(), right.getDesc()));
    }

    @Test
    public void diffAllTest() {
        ReturnOrder oldReturn = new ReturnOrder();
        ReturnOrder newReturn = new ReturnOrder();
        //ReturnReason
        oldReturn.setReturnReason(ReturnReason.BADGOODS);
        newReturn.setReturnReason(ReturnReason.BADGOODS);
        //Retu
        oldReturn.setReturnWay(ReturnWay.EXPRESS);
        newReturn.setReturnWay(ReturnWay.OTHER);
        //ReturnPrice
//        oldReturn.setReturnPrice(new ReturnPrice(false, new BigDecimal(10), new BigDecimal(20), null, null));
//        newReturn.setReturnPrice(new ReturnPrice(false, new BigDecimal(10), new BigDecimal(40), null, null));
        //ReturnItems
//        List<ReturnItem> newItems = Arrays.asList(
//                new ReturnItem("P123456", null, null, null, null,null,null,null, null, null,12, "", "", null,0L),
//                new ReturnItem("P123457", null, null, null, null,null,null,null, null, null,13, "", "", null,0L)
//        );
//        newReturn.setReturnItems(newItems);

        DiffResult diff = oldReturn.diff(newReturn);
        System.out.println(diff.getDiffs());

        oldReturn.buildDiffStr(newReturn).forEach(System.out::println);
    }


    public static void main(String[] args) {
//        testCommonReturnOrder();
        testNewPickReturnOrder();
    }

    private static void testNewPickReturnOrder() {
        ReturnOrder returnOrder = new ReturnOrder();
        ReturnPrice returnPrice = new ReturnPrice();
        returnOrder.setReturnPrice(returnPrice);
        List<ReturnItem> returnItems = new ArrayList<>();
        returnOrder.setReturnItems(returnItems);
        returnOrder.setActivityType(TradeActivityTypeEnum.NEWPICKTRADE.toActivityType());

        returnPrice.setTotalPrice(BigDecimal.valueOf(15));
        returnPrice.setShouldReturnCash(BigDecimal.valueOf(10));
        returnPrice.setActualReturnCash(BigDecimal.valueOf(10));
        returnPrice.setBalanceReturnPrice(BigDecimal.valueOf(5));
        returnPrice.setActualBalanceReturnPrice(BigDecimal.valueOf(5));

        ReturnItem returnItem1 = new ReturnItem();
        List<PickGoodsDTO> returnGoods1 = new ArrayList<>();
        returnItem1.setReturnGoodsList(returnGoods1);
        returnItems.add(returnItem1);

        ReturnItem returnItem2 = new ReturnItem();
        List<PickGoodsDTO> returnGoods2 = new ArrayList<>();
        returnItem2.setReturnGoodsList(returnGoods2);
        returnItems.add(returnItem2);

        //10: 4-6
        PickGoodsDTO pickGoodsDTO1 = new PickGoodsDTO();
        pickGoodsDTO1.setNewPileOrderNo("NP1");
        pickGoodsDTO1.setGoodsInfoId("NP1-SKU");
        pickGoodsDTO1.setReturnCashPrice(new BigDecimal("6"));
        pickGoodsDTO1.setActualReturnCashPrice(new BigDecimal("6"));

        pickGoodsDTO1.setReturnBalancePrice(new BigDecimal("4"));
        pickGoodsDTO1.setActualReturnBalancePrice(new BigDecimal("4"));
        returnGoods1.add(pickGoodsDTO1);

        //5: 1-4
        PickGoodsDTO pickGoodsDTO2 = new PickGoodsDTO();
        pickGoodsDTO2.setNewPileOrderNo("NP2");
        pickGoodsDTO2.setGoodsInfoId("NP2-SKU");
        pickGoodsDTO2.setReturnCashPrice(new BigDecimal("4"));
        pickGoodsDTO2.setActualReturnCashPrice(new BigDecimal("4"));

        pickGoodsDTO2.setReturnBalancePrice(new BigDecimal("1"));
        pickGoodsDTO2.setActualReturnBalancePrice(new BigDecimal("1"));
        returnGoods2.add(pickGoodsDTO2);

        BigDecimal modifyPrice = BigDecimal.valueOf(12);
        new ReturnOrderService().refillReturnBalancePriceAndCashPrice(returnOrder, modifyPrice);
        System.out.println(JSON.toJSONString(returnOrder));
    }

    private static void testCommonReturnOrder() {
        ReturnOrder returnOrder = new ReturnOrder();

        ReturnPrice returnPrice = new ReturnPrice();
        returnPrice.setTotalPrice(BigDecimal.valueOf(15));
        returnPrice.setShouldReturnCash(BigDecimal.valueOf(10));
        returnPrice.setActualReturnCash(BigDecimal.valueOf(10));
        returnPrice.setBalanceReturnPrice(BigDecimal.valueOf(5));
        returnPrice.setActualBalanceReturnPrice(BigDecimal.valueOf(5));

        returnOrder.setActivityType(TradeActivityTypeEnum.TRADE.toActivityType());
        returnOrder.setReturnPrice(returnPrice);

        BigDecimal modifyPrice = BigDecimal.valueOf(12);
        new ReturnOrderService().refillReturnBalancePriceAndCashPrice(returnOrder, modifyPrice);
        System.out.println(JSON.toJSONString(returnOrder));
    }
}
