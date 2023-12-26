package com.wanmi.sbc.shopcart.bean.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author lm
 * @date 2022/11/19 16:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PushFailLogDTO implements Serializable {


    /*订单号*/
    private String tid;

    /*推送状态*/
    private String pushState;

    /*订单创建时间*/
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime orderTime;

    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    /*推送时间*/
    private LocalDateTime createTime;

    /*失败原因*/
    private String failReason;

}
