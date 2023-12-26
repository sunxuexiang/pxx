package com.wanmi.sbc.goods.api.response.goodslabelrela;

import com.wanmi.sbc.goods.bean.vo.GoodsLabelRelaVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>邀新统计列表结果</p>
 * @author lvheng
 * @date 2021-04-23 14:20:19
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsLabelRelaListResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 邀新统计列表结果
     */
    @ApiModelProperty(value = "邀新统计列表结果")
    private List<GoodsLabelRelaVO> goodsLabelRelaVOList;
}
