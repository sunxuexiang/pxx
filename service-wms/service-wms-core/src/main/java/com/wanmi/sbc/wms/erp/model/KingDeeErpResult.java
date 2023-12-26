package com.wanmi.sbc.wms.erp.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 描述
 *
 * @author yitang
 * @version 1.0
 */
@Setter
@Getter
public class KingDeeErpResult implements Serializable {

    private static final long serialVersionUID = -1561963665417227431L;

    private String msg;

    private String code;
}
