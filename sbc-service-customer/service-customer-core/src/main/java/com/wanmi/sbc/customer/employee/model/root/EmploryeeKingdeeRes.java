package com.wanmi.sbc.customer.employee.model.root;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 描述
 *
 * @author yitang
 * @version 1.0
 */
@Getter
@Setter
public class EmploryeeKingdeeRes implements Serializable {

    private static final long serialVersionUID = -1732584563797777623L;

    private String code;

    private String msg;

    private KingdeeErpModel data;
}
