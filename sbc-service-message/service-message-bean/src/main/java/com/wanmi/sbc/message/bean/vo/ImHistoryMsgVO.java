package com.wanmi.sbc.message.bean.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author shiGuangYi
 * @createDate 2023-06-30 14:29
 * @Description: TODO
 * @Version 1.0
 */
@Data
@ApiModel
public class ImHistoryMsgVO implements Serializable {
    private long SdkAppId;
    private String ChatType;
    private String MsgTime;
    private List<MsgList> MsgList;
}
