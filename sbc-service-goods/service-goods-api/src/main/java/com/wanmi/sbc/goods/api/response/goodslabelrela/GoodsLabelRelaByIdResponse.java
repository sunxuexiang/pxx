package com.wanmi.sbc.goods.api.response.goodslabelrela;

import com.wanmi.sbc.goods.bean.vo.GoodsLabelRelaVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>根据id查询任意（包含已删除）邀新统计信息response</p>
 * @author lvheng
 * @date 2021-04-23 14:20:19
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsLabelRelaByIdResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 邀新统计信息
     */
    @ApiModelProperty(value = "邀新统计信息")
    private GoodsLabelRelaVO goodsLabelRelaVO;
}
