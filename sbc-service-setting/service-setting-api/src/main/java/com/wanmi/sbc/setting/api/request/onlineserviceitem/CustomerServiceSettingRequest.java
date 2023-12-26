package com.wanmi.sbc.setting.api.request.onlineserviceitem;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>客服服务信息配置数据</p>
 * @author zhouzhenguo
 * @date 2023-08-31 16:10:28
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerServiceSettingRequest implements Serializable {

    /**
     * 设置ID
     */
    @ApiModelProperty(value = "设置ID")
    private Long settingId;

    /**
     * 公司ID
     */
    @ApiModelProperty(value = "设置ID")
    private Long companyInfoId;

    /**
     * 店铺ID
     */
    @ApiModelProperty(value = "店铺ID")
    private Long storeId;

    /**
     * 消息内容
     */
    @ApiModelProperty(value = "消息配置内容")
    private CustomerServiceSettingContentVo content;

    /**
     * 设置类型：1、人工欢迎语；2、客户超时语；3、结束会话说辞；4、接收离线消息账号
     */
    @ApiModelProperty(value = "设置类型：1、人工欢迎语；2、客户超时语；3、结束会话说辞；4、接收离线消息账号")
    private Integer settingType;

    /**
     * 操作用户ID
     */
    @ApiModelProperty(value = "操作用户ID")
    private String operatorId;

}
