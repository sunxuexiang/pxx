package com.wanmi.sbc.es.elastic;

import com.wanmi.sbc.common.util.EsConstants;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/**
 * @ClassName ImMessage
 * @Description 腾讯IM消息封装实体类
 * @Author zhouzhenguo
 * @Date 2023/07/27 11:18
 **/
@Document(indexName = EsConstants.DOC_IM_MESSAGE)
@Data
public class EsImMessage implements Serializable {

    /**
     * 发送方账号
     */
    @Field(type = FieldType.Text)
    private String fromAccount;

    /**
     * 单聊消息：接收方方账号
     */
    @Field(type = FieldType.Text)
    private String toAccount;

    /**
     * 群聊消息：群ID
     */
    @Field(type = FieldType.Text)
    private String groupId;

    /**
     * 消息发送时间
     */
    @Field(type = FieldType.Long)
    private Long msgTimestamp;

    /**
     * 消息类型
     */
    @Field(type = FieldType.Text)
    private String msgType;

    /**
     * 消息内容
     */
    @Field(type = FieldType.Text)
    private String msgContent;

    /**
     * 店铺ID
     */
    @Field(type = FieldType.Long)
    private Long storeId;

    /**
     * 文件URL地址
     */
    @Field(type = FieldType.Text)
    private String fileUrl;

    /**
     * 发送类型：0、系统；1、客服；2、客户
     */
    private Integer sendType;

    /**
     * 引用消息内容JSON格式
     */
    private String quoteMessage;
}
