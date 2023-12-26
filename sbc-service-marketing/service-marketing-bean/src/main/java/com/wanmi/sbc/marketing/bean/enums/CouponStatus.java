package com.wanmi.sbc.marketing.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * 查询类型，0：全部，1：生效中，2：未生效，3：领取生效，4：已失效
 */
@ApiEnum
public enum CouponStatus{

    @ApiEnumProperty("0：全部")
    ALL,

    @ApiEnumProperty("1：生效中")
    STARTED,

    @ApiEnumProperty("2：未生效")
    NOT_START,

    @ApiEnumProperty("3：领取生效")
    DAYS,

    @ApiEnumProperty("4：已失效")
    ENDED;

    @JsonCreator
    public CouponStatus fromValue(int value) {
        return values()[value];
    }

    public static String getTipsStr(CouponStatus couponStatus) {
        String res = "";
        if(couponStatus != null){
            switch (couponStatus) {
                case ALL:
                    res = "全部";
                    break;
                case STARTED:
                    res = "生效中";
                    break;
                case NOT_START:
                    res = "未生效";
                    break;
                case DAYS:
                    res = "领取生效";
                    break;
                case ENDED:
                    res = "已失效";
                    break;
                default:
            }
        }
        return res;
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

}
