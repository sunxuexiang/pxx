package com.wanmi.sbc.walletorder.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * push金蝶状态枚举
 *
 * @author yitang
 * @version 1.0
 */
@ApiEnum
public enum PushKingdeeStatusEnum {
    @ApiEnumProperty("创建")
    CREATE(0),
    @ApiEnumProperty("推送成功")
    PUSHSUCCESS(1),
    @ApiEnumProperty("推送失败")
    FAILEDPUSH(2),
    @ApiEnumProperty("参数出错")
    PARAMETERERROR(3);

    private final Integer status;

    PushKingdeeStatusEnum(Integer status){
        this.status = status;
    }

    @JsonValue
    public Integer toStatus() {
        return status;
    }

}
