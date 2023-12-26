package com.wanmi.sbc.setting.api.request.imonlineservice;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

/**
 * <p>IM客服常用语</p>
 * @author zzg
 * @date 2023-06-05 16:10:28
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonChatMsgVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息内容
     */
    @ApiModelProperty(value = "消息内容")
    private String message;

    @ApiModelProperty(value = "排序，升序")
    private Integer sortNum;

    @ApiModelProperty(value = "类型：0、智能回复；1、快捷回复")
    private Integer msgType;

}
