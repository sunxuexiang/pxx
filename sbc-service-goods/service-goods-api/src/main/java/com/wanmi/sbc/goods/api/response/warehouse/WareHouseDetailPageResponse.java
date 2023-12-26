package com.wanmi.sbc.goods.api.response.warehouse;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.goods.bean.vo.WareHouseDetailVO;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>仓库表分页结果</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:21:37
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WareHouseDetailPageResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 仓库表列表结果
     */
    @ApiModelProperty(value = "仓库表列表结果")
    private List<WareHouseDetailVO> wareHouseDetailVOList;
}
