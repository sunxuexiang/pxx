package com.wanmi.sbc.order.api.response.trade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/6/1 10:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TradeCoinReturnVerifyResponse implements Serializable {
    private static final long serialVersionUID = 3145515430430944492L;

    private Boolean canReturnFlag;

    private String msg;
}
