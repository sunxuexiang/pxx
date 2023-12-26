package com.wanmi.sbc.goods.api.response.warehouse;

import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p>仓库表列表结果</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:21:37
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WareHouseMapResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 仓库表列表结果
     */
    @ApiModelProperty(value = "仓库表列表结果,以店铺id为key")
    private Map<Long,List<WareHouseVO>> wareHouseVOMap;
}
