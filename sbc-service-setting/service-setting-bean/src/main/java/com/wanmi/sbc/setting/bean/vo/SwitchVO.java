package com.wanmi.sbc.setting.bean.vo;

import com.wanmi.sbc.common.enums.DeleteFlag;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @desc  
 * @author shiy  2023/12/5 8:56
*/
@Data
@Builder
public class SwitchVO implements Serializable {

    /**
     * 主键
     */

    private String id;

    /**
     * 开关名称
     */
    private String switchName;

    /**
     *开关状态 0：关闭 1：开启
     */
    private Integer status;

    /**
     * 删除标志
     */
    private DeleteFlag delFlag;

}
