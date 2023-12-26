package com.wanmi.sbc.goods.api.response.goodslabelrela;

import com.wanmi.sbc.goods.bean.vo.GoodsLabelRelaVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>邀新统计新增结果</p>
 * @author lvheng
 * @date 2021-04-23 14:20:19
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsLabelRelaAddResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 已新增的邀新统计信息
     */
    @ApiModelProperty(value = "已新增的邀新统计信息")
    private GoodsLabelRelaVO goodsLabelRelaVO;
}
