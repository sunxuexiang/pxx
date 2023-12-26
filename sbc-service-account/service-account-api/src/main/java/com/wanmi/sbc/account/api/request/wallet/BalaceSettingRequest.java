package com.wanmi.sbc.account.api.request.wallet;

import lombok.Data;

import java.io.Serializable;

@Data
public class BalaceSettingRequest implements Serializable {

    /**
     * 主键（更细标识）
     */
    private Long id;

    /**
     * 说明
     */
    private String settingKey;
    /**
     * {@link com.wanmi.sbc.account.bean.enums.BalanceSettingTypeEnum}
     * 配置类型
     */
    private String settingValue;
}
