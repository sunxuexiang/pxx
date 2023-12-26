package com.wanmi.sbc.setting.api.request.sysswitch;

import com.wanmi.sbc.common.enums.DeleteFlag;
import lombok.*;

import java.io.Serializable;

/**
 * @desc  
 * @author shiy  2023/12/5 8:56
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwitchRequest implements Serializable {

    /**
     * 主键
     */

    private String id;

    private String switchCode;

    /**
     * 开关名称
     */
    private String switchName;

    /**
     *开关状态 0：关闭 1：开启
     */
    private Integer status;
}
