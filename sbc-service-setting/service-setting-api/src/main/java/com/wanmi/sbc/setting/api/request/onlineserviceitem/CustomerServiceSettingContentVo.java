package com.wanmi.sbc.setting.api.request.onlineserviceitem;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class CustomerServiceSettingContentVo {

    /**
     * 聊天语内容：欢迎语、超时提示语、结束会话语
     */
    @ApiModelProperty(value = "聊天语内容：欢迎语、客服超时提示语、结束会话语")
    private String message;

    /**
     * 时间：客服超时时间
     */
    @ApiModelProperty(value = "时间：客服超时时间")
    private Integer time;

    /**
     * 离线设置，客服全部离线时，接收用户发起聊天的客服账号列表
     */
    @ApiModelProperty(value = "离线设置，客服全部离线时，接收用户发起聊天的客服账号列表")
    private String[] offlineReceiveAccounts;

    @ApiModelProperty(value = "开关状态：true/false")
    private boolean switchStatus;

    @ApiModelProperty(value = "最大接待客户数量")
    private Integer quantity;

}
