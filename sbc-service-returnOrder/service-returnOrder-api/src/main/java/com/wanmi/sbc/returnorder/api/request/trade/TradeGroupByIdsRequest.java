package com.wanmi.sbc.returnorder.api.request.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class TradeGroupByIdsRequest implements Serializable {
    private static final long serialVersionUID = 2027256835226018496L;


    /**
     * groupid集合
     */
    @ApiModelProperty(value = "groupid集合")
    private List<String> groupId;
}
