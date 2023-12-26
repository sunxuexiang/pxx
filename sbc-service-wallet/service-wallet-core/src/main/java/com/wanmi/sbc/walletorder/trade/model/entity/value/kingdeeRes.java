package com.wanmi.sbc.walletorder.trade.model.entity.value;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 金蝶登录返回
 *
 * @author yitang
 * @version 1.0
 */
@Getter
@Setter
public class kingdeeRes implements Serializable {

    private static final long serialVersionUID = 5067891239571535575L;

    private String code;

    private String msg;

    private String data;
}
