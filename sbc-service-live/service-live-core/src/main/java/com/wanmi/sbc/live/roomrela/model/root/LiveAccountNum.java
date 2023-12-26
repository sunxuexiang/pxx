package com.wanmi.sbc.live.roomrela.model.root;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * <p>直播账户绑定直播间数量</p>
 * @author zhouzhenguo
 * @date 2023-07-12 16:27:45
 */
@Data
@ToString(callSuper = true)
public class LiveAccountNum {

    /**
     * 直播账号用户ID
     */
    private String customerId;

    /**
     * 统计数量
     */
    private Integer quantity;
}
