package com.wanmi.sbc.goods.api.response.pointsgoodscate;

import com.wanmi.sbc.goods.bean.vo.PointsGoodsCateVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>积分商品分类表列表结果</p>
 * @author yang
 * @date 2019-05-13 09:50:07
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointsGoodsCateListResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 积分商品分类表列表结果
     */
    @ApiModelProperty(value = "积分商品分类表列表结果")
    private List<PointsGoodsCateVO> pointsGoodsCateVOList;
}
