package com.wanmi.sbc.setting.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>腾讯会话参数</p>
 * @Author shiGuangYi
 * @createDate 2023-06-19 20:36
 * @Description: TODO
 * @Version 1.0
 */
@ApiModel
@Data
public class RecentContactVO implements Serializable {

    @ApiModelProperty(value = "会话参数")
    private List<SessionItemDO> sessionItem;
    @ApiModelProperty(value = "结束标识：1 表示已返回全量会话，0 表示还有会话没拉完")
    private int completeFlag;
    @ApiModelProperty(value = "普通会话下一页拉取的起始时间，分页拉取时通过请求包的 TimeStamp 字段带给移动通信后台")
    private long timeStamp;
    @ApiModelProperty(value = "普通会话下一页拉取的起始位置，分页拉取时通过请求包的 StartIndex 字段带给移动通信后台")
    private int startIndex;
    @ApiModelProperty(value = "置顶会话下一页拉取的起始时间，分页拉取时通过请求包的 TopTimeStamp 字段带给移动通信后台")
    private long topTimeStamp;
    @ApiModelProperty(value = "置顶会话下一页拉取的起始位置，分页拉取时通过请求包的 TopStartIndex 字段带给移动通信后台")
    private int topStartIndex;
    @ApiModelProperty(value = "请求包的处理结果，OK 表示处理成功，FAIL 表示失败")
    private String actionStatus;
    @ApiModelProperty(value = "错误码，0表示成功，非0表示失败")
    private int errorCode;
    @ApiModelProperty(value = "明细信息")
    private String errorInfo;
    @ApiModelProperty(value = "详细的客户端展示信息")
    private String errorDisplay;

    @ApiModelProperty(value = "用户信息")
    private List<ProfileItemDO> profileItem;


}
