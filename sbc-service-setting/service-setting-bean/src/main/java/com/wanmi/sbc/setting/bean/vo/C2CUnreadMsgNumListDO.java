package com.wanmi.sbc.setting.bean.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Author shiGuangYi
 * @createDate 2023-06-20 11:29
 * @Description: TODO
 * @Version 1.0
 */
@ApiModel
@Data
public class C2CUnreadMsgNumListDO {
    private String peerAccount;
    private int c2CUnreadMsgNum;
}
