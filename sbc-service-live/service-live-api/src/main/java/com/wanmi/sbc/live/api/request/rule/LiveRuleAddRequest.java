package com.wanmi.sbc.live.api.request.rule;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import lombok.*;

@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveRuleAddRequest extends BaseRequest {
    private static final long serialVersionUID = 1L;

    /**
     * 直播间id
     */
    private Integer liveRoomId;
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
