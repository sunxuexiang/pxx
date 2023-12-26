package com.wanmi.sbc.goods.api.response.goods;

import com.wanmi.sbc.goods.bean.vo.GoodsExportVO;
import com.wanmi.sbc.goods.bean.vo.StoreGoodsExportVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 商品导出的列表
 * Created by lf on 2020/12/30.
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreGoodsExportListResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 导出的商品信息
     */
    @ApiModelProperty(value = "导出的商品信息")
    private List<StoreGoodsExportVO> goodsExports;
}
