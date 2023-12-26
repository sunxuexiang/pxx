package com.wanmi.sbc.goods.api.response.goodslabel;

import com.wanmi.sbc.goods.bean.vo.GoodsLabelVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>导航配置列表结果</p>
 * @author lvheng
 * @date 2021-04-19 11:09:28
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsLabelListResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 导航配置列表结果
     */
    @ApiModelProperty(value = "导航配置列表结果")
    private List<GoodsLabelVO> goodsLabelVOList;
}
