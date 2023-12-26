package com.wanmi.sbc.common.constant;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 区域
 *
 * @author yitang
 * @version 1.0
 */
@Setter
@Getter
public class Area implements Serializable {
    /**
     * 编码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 父亲编码
     */
    private String parent_code;
}
