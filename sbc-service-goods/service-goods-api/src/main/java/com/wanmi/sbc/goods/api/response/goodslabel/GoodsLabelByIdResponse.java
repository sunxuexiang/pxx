package com.wanmi.sbc.goods.api.response.goodslabel;

import com.wanmi.sbc.goods.bean.vo.GoodsLabelVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>根据id查询任意（包含已删除）导航配置信息response</p>
 * @author lvheng
 * @date 2021-04-19 11:09:28
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsLabelByIdResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 导航配置信息
     */
    @ApiModelProperty(value = "导航配置信息")
    private GoodsLabelVO goodsLabelVO;
}
