package com.wanmi.sbc.goods.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;
import com.wanmi.sbc.common.constant.ErrorCodeConstant;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.goods.bean.vo.DeliverWayVO;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 配送方式
 */
@ApiEnum
@AllArgsConstructor
@NoArgsConstructor
public enum DeliverWay {

    @ApiEnumProperty("******作废******0: 其他")
    OTHER("其他",0),

    /**
     * @desc  1: 托运部
     */
    @ApiEnumProperty("1: 托运部")
    LOGISTICS("托运部",1),

    /**
     * @desc  2: 快递到家(自费)
     */
    @ApiEnumProperty("2: 快递到家(自费)")
    EXPRESS("快递到家（自费）",0),

    @ApiEnumProperty("******作废******3: 自提")
    PICK_SELF("自提",0),

    /**
     * @desc  4: 配送到家（自费）
     */
    @ApiEnumProperty("4: 配送到家（自费）")
    DELIVERY_HOME("免费店配",0),
    /**
     * 湖北物流免店配设置 超过配送范围站点自提
     */
    @ApiEnumProperty("******作废******5: 站点自提")
    SITE_PICK_SELF("站点自提",0),
    /**
     * 6: 上门自提
     */
    @ApiEnumProperty("6: 上门自提")
    TO_DOOR_PICK("上门自提",1),
    /**
     * 7: 配送到店（自费）
     */
    @ApiEnumProperty("7: 配送到店（自费）")
    DELIVERY_TO_STORE("配送到店（自费）",1),
    /**
     * @desc  8: 指定物流
     */
    @ApiEnumProperty("8: 指定专线")
    SPECIFY_LOGISTICS("指定专线",1),
    /**
     * @desc  9: 同城配送（自费）
     */
    @ApiEnumProperty("9: 同城配送（到付）")
    INTRA_CITY_LOGISTICS("同城配送（到付）",1),

    /**
     * @desc  10: 快递到家（到付）
     */
    @ApiEnumProperty("10: 快递到家（到付）")
    EXPRESS_ARRIVED("快递到家（到付）",1),

    /**
     * @desc  11: 快递到家（自费）
     */
    @ApiEnumProperty("11: 快递到家(自费)")
    EXPRESS_SELF_PAID("快递到家（自费）",1),;


    private String desc;
    private Integer enableFlag;

    @JsonCreator
    public static DeliverWay fromValue(int value) {
        return values()[value];
    }

    public static Integer getSize() {
        return values().length;
    }

//  @JsonCreator
//  public DeliverWay fromValue(int value) {
//      return values()[value];
//  }
    @JsonValue
    public Integer toValue() {
        return this.ordinal();
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(Integer enableFlag) {
        this.enableFlag = enableFlag;
    }

    public static List<DeliverWayVO> getEnableList(){
        List<DeliverWayVO> deliverWayList = new ArrayList<>(DeliverWay.values().length);
        for(DeliverWay deliverWay:DeliverWay.values()){
            if(deliverWay.getEnableFlag()==1){
                deliverWayList.add(DeliverWayVO.builder().deliveryTypeId(deliverWay.toValue()).deliverWayDesc(deliverWay.getDesc()).build());
            }
        }
        return deliverWayList;
    }

    public static void checkDeliveryWay(DeliverWay deliverWay){
        boolean isTrue = deliverWay.getEnableFlag()==1;
        if(!isTrue){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"APP版本太低,请升级APP再操作");
        }
    }

    public static void checkDeliveryWay(Integer deliverWayValue){
        if(0>deliverWayValue|| getSize()<deliverWayValue){
            return;
        }
        DeliverWay deliverWay = fromValue(deliverWayValue);
        checkDeliveryWay(deliverWay);
    }

    public static Boolean isLogistics(DeliverWay deliverWay){
        return DeliverWay.LOGISTICS.equals(deliverWay) || DeliverWay.SPECIFY_LOGISTICS.equals(deliverWay);
    }

    public static Boolean isLogistics(Integer deliverWayValue){
        if(0>deliverWayValue|| getSize()<deliverWayValue){
            return false;
        }
        DeliverWay deliverWay = fromValue(deliverWayValue);
        return isLogistics(deliverWay);
    }

    public static Boolean isLogisticsTYB(Integer deliverWayValue){
        if(0>deliverWayValue|| getSize()<deliverWayValue){
            return false;
        }
        DeliverWay deliverWay = fromValue(deliverWayValue);
        return isDeliveryTYB(deliverWay);
    }

    public static Boolean isLogisticsZDZX(Integer deliverWayValue){
        if(0>deliverWayValue|| getSize()<deliverWayValue){
            return false;
        }
        DeliverWay deliverWay = fromValue(deliverWayValue);
        return isDeliveryZDZX(deliverWay);
    }

    public static Boolean isDeliverySMZT(Integer deliverWayValue){
        if(0>deliverWayValue|| getSize()<deliverWayValue){
            return false;
        }
        DeliverWay deliverWay = fromValue(deliverWayValue);
        return isDeliverySMZT(deliverWay);
    }

    public static Boolean isDeliveryTCPS(Integer deliverWayValue){
        if(0>deliverWayValue|| getSize()<deliverWayValue){
            return false;
        }
        DeliverWay deliverWay = fromValue(deliverWayValue);
        return isDeliveryTCPS(deliverWay);
    }

    public static Boolean isExpressArrived(Integer deliverWayValue){
        if(0>deliverWayValue|| getSize()<deliverWayValue){
            return false;
        }
        DeliverWay deliverWay = fromValue(deliverWayValue);
        return isExpressArrived(deliverWay);
    }

    public static boolean isTmsDelivery(Integer deliverWayValue) {
        if(0>deliverWayValue|| getSize()<deliverWayValue){
            return false;
        }
        DeliverWay deliverWay = fromValue(deliverWayValue);
        return isTmsDelivery(deliverWay);
    }

    public static Boolean isExpressToWMS(DeliverWay deliverWay){
        return DeliverWay.EXPRESS.equals(deliverWay) || DeliverWay.INTRA_CITY_LOGISTICS.equals(deliverWay)||DeliverWay.EXPRESS_ARRIVED.equals(deliverWay)||isExpressSelfPaid(deliverWay);
    }

    public static Boolean isExpress(DeliverWay deliverWay){
        return DeliverWay.EXPRESS.equals(deliverWay) || isExpressSelfPaid(deliverWay);
    }

    public static Boolean logisticFeeBeginPaid(DeliverWay deliverWay){
        return isDeliverySFKD(deliverWay) || isDeliveryToStore(deliverWay) ||isExpressSelfPaid(deliverWay);
    }

    public static Boolean deliverFeeNeed(DeliverWay deliverWay){
        return DeliverWay.EXPRESS.equals(deliverWay) || DeliverWay.DELIVERY_TO_STORE.equals(deliverWay) ||DeliverWay.EXPRESS_ARRIVED.equals(deliverWay);
    }

    public static boolean isDeliveryToStore(DeliverWay deliverWay) {
        return DeliverWay.DELIVERY_TO_STORE.equals(deliverWay);
    }
    public static boolean isDeliveryTYB(DeliverWay deliverWay) {
        return DeliverWay.LOGISTICS.equals(deliverWay);
    }

    public static boolean isDeliveryZDZX(DeliverWay deliverWay) {
        return DeliverWay.SPECIFY_LOGISTICS.equals(deliverWay);
    }

    public static boolean isDeliveryTCPS(DeliverWay deliverWay) {
        return DeliverWay.INTRA_CITY_LOGISTICS.equals(deliverWay);
    }

    public static boolean isExpressArrived(DeliverWay deliverWay) {
        return DeliverWay.EXPRESS_ARRIVED.equals(deliverWay);
    }

    public static boolean isDeliverySFKD(DeliverWay deliverWay) {
        return DeliverWay.EXPRESS.equals(deliverWay);
    }

    public static boolean isExpressSelfPaid(DeliverWay deliverWay) {
        return DeliverWay.EXPRESS_SELF_PAID.equals(deliverWay);
    }

    public static boolean isDeliveryMFDP(DeliverWay deliverWay) {
        return DeliverWay.DELIVERY_HOME.equals(deliverWay);
    }

    public static boolean isDeliverySMZT(DeliverWay deliverWay) {
        return DeliverWay.TO_DOOR_PICK.equals(deliverWay);
    }

    public static boolean isTmsDelivery(DeliverWay deliverWay) {
        return DeliverWay.DELIVERY_TO_STORE.equals(deliverWay) ||  DeliverWay.EXPRESS_SELF_PAID.equals(deliverWay);
    }

    /**
     * 是否需要建行 分账
     * @param deliverWay
     * @return
     */
    public static boolean isCcbSubBill(DeliverWay deliverWay) {
        return DeliverWay.DELIVERY_TO_STORE.equals(deliverWay) ||  DeliverWay.EXPRESS_SELF_PAID.equals(deliverWay);
    }
}
