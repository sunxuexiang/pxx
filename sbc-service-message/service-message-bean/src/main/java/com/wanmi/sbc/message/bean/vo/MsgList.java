package com.wanmi.sbc.message.bean.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @Author shiGuangYi
 * @createDate 2023-06-30 14:30
 * @Description: TODO
 * @Version 1.0
 */
@Data
@ApiModel
public class MsgList {
    private String From_Account;
    private String To_Account;
    private String MsgTimestamp;
    private String MsgSeq;
    private String MsgRandom;
    private List<MsgBody> MsgBody;
}
