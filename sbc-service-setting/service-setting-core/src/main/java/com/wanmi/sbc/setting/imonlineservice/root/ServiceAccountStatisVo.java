package com.wanmi.sbc.setting.imonlineservice.root;

import lombok.Data;


/**
 * <p>IM客服常用语</p>
 * @author zzg
 * @date 2023-08-24 16:10:28
 */
@Data
public class ServiceAccountStatisVo {

    /**
     * 客服账号
     */
    private String serverAccount;

    /**
     * 接待数量
     */
    private Integer quantity;
}
