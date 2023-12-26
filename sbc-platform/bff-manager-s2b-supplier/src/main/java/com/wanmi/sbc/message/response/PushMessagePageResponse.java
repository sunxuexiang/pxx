package com.wanmi.sbc.message.response;

import com.wanmi.sbc.common.base.MicroServicePage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>站内信任务表分页结果</p>
 * @author xuyunpeng
 * @date 2020-01-06 11:12:11
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushMessagePageResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 站内信任务表分页结果
     */
    @ApiModelProperty(value = "站内信任务表分页结果")
    private MicroServicePage<PushMessageResponse> pushMessagePage;

}
