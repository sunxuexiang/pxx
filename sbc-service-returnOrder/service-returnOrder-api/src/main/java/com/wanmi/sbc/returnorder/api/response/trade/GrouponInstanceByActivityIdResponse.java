package com.wanmi.sbc.returnorder.api.response.trade;

import com.wanmi.sbc.returnorder.bean.vo.GrouponInstanceVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by feitingting on 2019/8/12.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class GrouponInstanceByActivityIdResponse {
    /**
     * 团信息
     */
    @ApiModelProperty(value = "团信息")
    private GrouponInstanceVO grouponInstance;

    private String customerName;
}
