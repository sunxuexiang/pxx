package com.wanmi.sbc.setting.api.request.advertising;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @description: 删除请求类
 * @author: XinJiang
 * @time: 2022/3/31 11:57
 */
@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartPageAdvertisingDelByIdRequest implements Serializable {

    private static final long serialVersionUID = 4456620545308083499L;

    /**
     * 首页广告位id
     */
    @ApiModelProperty(value = "首页广告位id")
    private String advertisingId;

    /**
     * 删除人
     */
    @ApiModelProperty(value = "删除人")
    private String delPerson;
}
