package com.wanmi.sbc.returnorder.api.response.ordertrack;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName OrderTrackResp
 * @Description TODO
 * @Author shiy
 * @Date 2023/6/17 10:09
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class OrderTrackListResp implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 轨迹集合
     */
    @ApiModelProperty("轨迹集合")
    private List<OrderTrackResp> orderTrackRespList;

}
