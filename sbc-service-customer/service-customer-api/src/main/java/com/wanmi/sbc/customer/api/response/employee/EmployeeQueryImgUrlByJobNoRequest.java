package com.wanmi.sbc.customer.api.response.employee;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>描述<p>
 *
 * @author zhaowei
 * @date 2021/6/10
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeQueryImgUrlByJobNoRequest implements Serializable {

    /**
     * 业务员小程序分享图片
     */
    @ApiModelProperty(value = "微信小程序分享图片地址")
    private String wechatImgUrl;

}
