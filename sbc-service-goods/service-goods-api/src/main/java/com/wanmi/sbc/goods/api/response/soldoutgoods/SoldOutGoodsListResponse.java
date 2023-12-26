package com.wanmi.sbc.goods.api.response.soldoutgoods;

import com.wanmi.sbc.goods.bean.vo.SoldOutGoodsVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>类目品牌排序表列表结果</p>
 * @author lvheng
 * @date 2021-04-10 15:09:50
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoldOutGoodsListResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 类目品牌排序表列表结果
     */
    @ApiModelProperty(value = "类目品牌排序表列表结果")
    private List<SoldOutGoodsVO> soldOutGoodsVOList;
}
