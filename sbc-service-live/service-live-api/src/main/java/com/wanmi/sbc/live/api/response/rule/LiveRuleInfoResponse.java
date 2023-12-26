package com.wanmi.sbc.live.api.response.rule;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveRuleInfoResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 1 在线人数 2点赞数量
     */
    private Integer type;

    /**
     * 规则起始人数
     */
    private Integer beginNum;
    /**
     * 增长系数以,隔开
     */
    private String coefficient;

    /**
     * 固定增加数量
     */
    private Integer fixed;
}
