package com.wanmi.sbc.customer.service.model;

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
public class CustomerKingdeeRes implements Serializable {

    private static final long serialVersionUID = 3601419997735511376L;

    private String code;

    private String msg;

    private String data;
}
