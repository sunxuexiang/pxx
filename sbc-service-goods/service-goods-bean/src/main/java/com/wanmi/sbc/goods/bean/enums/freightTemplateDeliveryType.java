package com.wanmi.sbc.goods.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@ApiEnum
@AllArgsConstructor
@NoArgsConstructor
public enum freightTemplateDeliveryType {

    /**
     * @desc  0：免费店配
    */
    @ApiEnumProperty("0: 免费店配")
    CONVENTION("免费店配"),

    /**
     * @desc  1：乡镇件
    */
    @ApiEnumProperty("1: 乡镇件")
    AREATENDELIVER,

    /**
     * @desc  2：第三方物流
    */
    @ApiEnumProperty("2: 第三方物流")
    THIRDLOGISTICS("托运部"),

    /**
     * @desc  3：上门自提
    */
    @ApiEnumProperty("3: 上门自提")
    TODOORPICK("上门自提"),

    /**
     * @desc  4：收费快递/快递到家(自费)
    */
    @ApiEnumProperty("4: 收费快递/快递到家(自费)")
    PAIDEXPRESS("快递到家（自费）"),

    /**
     * @desc 5：配送到店(自费)
    */
    @ApiEnumProperty("5: 配送到店(自费)")
    DELIVERYTOSTORE("配送到店（自费）"),

    /**
     * @desc :6：配送到店5件
    */
    @ApiEnumProperty("6: 配送到店5件")
    DELIVERYTOSTORE_5,

    /**
     * @desc :7：配送到店10件
    */
    @ApiEnumProperty("7: 配送到店10件")
    DELIVERYTOSTORE_10,

    /**
     * @desc :8：指定物流
    */
    @ApiEnumProperty("8: 指定物流")
    SPECIFY_LOGISTICS("指定专线"),

    /**
     * @desc  9：同城配送(自费)
    */
    @ApiEnumProperty("9: 同城配送(自费)")
    INTRA_CITY_LOGISTICS("同城配送（到付）"),
    /**
     * @desc  10: 快递到家(到付)
     */
    @ApiEnumProperty("10: 快递到家(到付)")
    EXPRESS_ARRIVED("快递到家（到付）"),

    @ApiEnumProperty("11: 收费快递/快递到家(自费)")
    EXPRESS_SELF_PAID("快递到家（自费）");

    private String desc;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    @JsonCreator
    public freightTemplateDeliveryType fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

    public static boolean isDeliverytostore5(freightTemplateDeliveryType ft){
        return freightTemplateDeliveryType.DELIVERYTOSTORE_5.equals(ft);
    }

    public static boolean isDeliverytostore10(freightTemplateDeliveryType ft){
        return freightTemplateDeliveryType.DELIVERYTOSTORE_10.equals(ft);
    }


    public static boolean isStoreVillage(freightTemplateDeliveryType ft){
        return freightTemplateDeliveryType.DELIVERYTOSTORE_10.equals(ft);
    }
}
