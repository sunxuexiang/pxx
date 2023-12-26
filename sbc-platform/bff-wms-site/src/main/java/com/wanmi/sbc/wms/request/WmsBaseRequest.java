package com.wanmi.sbc.wms.request;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author baijianzhong
 * @ClassName WmsBaseRequest
 * @Date 2020-07-15 17:36
 * @Description TODO
 **/
@Data
public class WmsBaseRequest implements Serializable {

    /**
     * 方法名
     */
    @ApiModelProperty(name = "方法名")
    @JSONField(name = "method")
    private String method;

    /**
     * 客户的Id，固定值：XYY
     */
    @ApiModelProperty(name = "客户Id")
    @JSONField(name = "client_customerid")
    private String client_customerid;

    /**
     * 传输的格式
     */
    @ApiModelProperty(name = "传输的格式")
    @JSONField(name = "client_db")
    private String client_db;

    /**
     * 版本号
     */
    @ApiModelProperty(name = "版本号")
    @JSONField(name = "messageid")
    private String messageid;

    /**
     * appkey
     */
    @ApiModelProperty(name = "appkey")
    @JSONField(name = "appkey")
    private String appkey;

    /**
     * appSecret
     */
    @ApiModelProperty(name = "appSecret")
    @JSONField(name = "apptoken")
    private String apptoken;

    /**
     * 调用时间
     */
    @ApiModelProperty(name = "调用时间")
    @JSONField(name = "timestamp")
    private String timestamp;

    /**
     * 加签标志
     */
    @ApiModelProperty(name = "加签标识")
    @JSONField(name = "sign")
    private String sign;

    /**
     * 应用级请求参数
     */
    @ApiModelProperty(name = "应用级请求参数")
    @JSONField(name = "data")
    private String data;
}
