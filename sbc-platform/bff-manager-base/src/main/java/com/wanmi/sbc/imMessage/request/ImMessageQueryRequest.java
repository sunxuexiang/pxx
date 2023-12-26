package com.wanmi.sbc.imMessage.request;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/**
 * 腾讯IM历史消息查询请求参数
 * @author zhouzhenguo
 * @dateTime 20230727
 */
@ApiModel
@Data
public class ImMessageQueryRequest implements Serializable {

    /**
     * 发送方账号
     */
    @ApiModelProperty(value = "发送方账号")
    private String fromAccount;

    /**
     * 接收方账号
     */
    @ApiModelProperty(value = "接收方账号")
    private String toAccount;

    @ApiModelProperty(value = "群聊消息：群ID")
    private String groupId;


    @ApiModelProperty(value = "聊天内容")
    private String msgContent;

    /**
     * 消息类型
     */
    @ApiModelProperty(value = "消息类型: 不传查询全部； TIMTextElem：文本消息； TIMFileElem：文件； TIMVideoFileElem：视频； TIMImageElem： 图片")
    private String msgType;

    /**
     * 分页条数
     */
    @ApiModelProperty(value = "分页条数")
    private int pageSize;

    /**
     * 当前页数
     */
    @ApiModelProperty(value = "当前页数")
    private int pageNum = 10;

    @ApiModelProperty(value = "搜索开始时间：yyyy-MM-dd HH:mm:ss")
    private String startTime;

    @ApiModelProperty(value = "搜索截止时间：yyyy-MM-dd HH:mm:ss")
    private String endTime;

    private Long storeId;
}
