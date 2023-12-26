package com.wanmi.sbc.setting.api.request.advertising;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.setting.bean.enums.AdvertisingType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 修改散批广告位状态请求参数类
 * @author: XinJiang
 * @time: 2022/4/19 11:09
 */
@ApiModel
@Data
public class AdvertisingRetailModifyStatusRequest implements Serializable {

    private static final long serialVersionUID = -6254980537435475623L;

    /**
     * 散批广告位id
     */
    @ApiModelProperty(value = "启动页广告位id")
    private String advertisingId;

    @ApiModelProperty(value = "状态 0关闭 1开启")
    private DefaultFlag status;

    @ApiModelProperty(value = "广告位类型")
    private AdvertisingType advertisingType;
}
