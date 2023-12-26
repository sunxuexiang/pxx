package com.wanmi.sbc.goods.api.request.info;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;
import java.util.List;

@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsInfoListByParentIdsRequest implements Serializable {

    private static final long serialVersionUID = -2265501195719873212L;


    @ApiModelProperty(value = "仓库Id")
    private Long wareId;

    /**
     * goodsInfoParentsIds
     */
    @ApiModelProperty(value = "goodsInfoParentsIds")
    private List<String> goodsInfoParentsIds;
}
