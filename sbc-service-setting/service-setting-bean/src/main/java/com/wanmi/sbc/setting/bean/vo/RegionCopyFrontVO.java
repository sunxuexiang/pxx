package com.wanmi.sbc.setting.bean.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @desc  
 * @author shiy  2023/12/8 14:53
*/
@Data
public class RegionCopyFrontVO implements Serializable {

    private String code;

    /**
     * 城市名称
     */
    private String name;

    /**
     * 上一级编号
     */
    private String parent_code;

}
