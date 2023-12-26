package com.wanmi.sbc.live.api.response.room;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>直播账户绑定直播间数量</p>
 * @author zhouzhenguo
 * @date 2023-07-12 16:27:45
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveAccountNumResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 直播账号用户ID
     */
    private String customerId;

    /**
     * 统计数量
     */
    private Integer quantity;

}
