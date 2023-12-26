package com.wanmi.sbc.returnorder.trade.model.entity.value;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 金蝶接口返回
 *
 * @author yitang
 * @version 1.0
 */
@Setter
@Getter
public class KingDeeResult implements Serializable {

    private static final long serialVersionUID = -49271339769096685L;

    private String msg;

    private String code;

}
