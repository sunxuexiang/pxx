package com.wanmi.sbc.setting.api.response.imonlineservice;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>客服开关实体类</p>
 * @author zhouzhenguo
 * @date 20230729
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerServiceSwitchResponse implements Serializable {


    private Long id;

    /**
     * 公司ID
     */
    @ApiModelProperty(value = "公司ID")
    private Long companyInfoId;

    /**
     * 在线客服启用类型：0、全部未启用；1、腾讯IM；2、智齿
     */
    @ApiModelProperty(value = "在线客服启用类型：0、全部未启用；1、腾讯IM；2、智齿")
    private Integer serviceSwitchType;

    @ApiModelProperty(value = "是否需要程序初始化IM客服账号")
    private boolean initIMAccount = false;
}
