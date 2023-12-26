package com.wanmi.sbc.customer.employee.model.root;

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
    private static final long serialVersionUID = -6351345177386019908L;

    private String msg;

    private String code;
}
