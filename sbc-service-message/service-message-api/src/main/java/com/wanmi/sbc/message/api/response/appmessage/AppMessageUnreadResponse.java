package com.wanmi.sbc.message.api.response.appmessage;

import com.wanmi.sbc.message.bean.vo.AppMessageUnreadVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>App站内信消息未读通知、优惠活动消息数量</p>
 * @author zhouzhenguo
 * @date 2023-07-03 10:53:00
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppMessageUnreadResponse implements Serializable {

    @ApiModelProperty(value = "服务通知消息数据")
    private AppMessageUnreadVo noticeMessage;

    @ApiModelProperty(value = "优惠促销消息数据")
    private AppMessageUnreadVo preferentialMessage;

}
