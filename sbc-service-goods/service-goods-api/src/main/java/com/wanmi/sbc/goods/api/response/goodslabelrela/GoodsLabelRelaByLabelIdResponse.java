package com.wanmi.sbc.goods.api.response.goodslabelrela;

import com.wanmi.sbc.goods.bean.vo.GoodsLabelRelaVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

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
public class GoodsLabelRelaByLabelIdResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 邀新统计列表结果
     */
    @ApiModelProperty(value = "商品标签列表结果")
    private List<GoodsLabelRelaVO> goodsLabelRelaVOList;
}
