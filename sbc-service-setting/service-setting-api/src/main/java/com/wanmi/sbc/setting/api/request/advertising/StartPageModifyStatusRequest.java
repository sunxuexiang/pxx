package com.wanmi.sbc.setting.api.request.advertising;

import com.wanmi.sbc.common.enums.DefaultFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 修改启动页广告配置信息状态请求实体类
 * @author: XinJiang
 * @time: 2022/3/31 15:33
 */
@ApiModel
@Data
public class StartPageModifyStatusRequest  implements Serializable {

    private static final long serialVersionUID = -5924958344523177666L;

    /**
     * 启动页广告位id
     */
    @ApiModelProperty(value = "启动页广告位id")
    private String advertisingId;

    @ApiModelProperty(value = "状态 0关闭 1开启")
    private DefaultFlag status;
}
