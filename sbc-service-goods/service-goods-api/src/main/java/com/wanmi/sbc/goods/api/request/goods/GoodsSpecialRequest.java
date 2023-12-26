package com.wanmi.sbc.goods.api.request.goods;

import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.goods.bean.dto.SpecialGoodsAddDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

/**
 * 特价商品新增请求实体
 * Created by baijz on 2017/3/24.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsSpecialRequest extends BaseRequest {

    /**
     * 批次号
     */
    @ApiModelProperty(value = "批次号")
    private List<SpecialGoodsAddDTO> specialGoodsList;

    /**
     * erp编码
     */
    @ApiModelProperty(value = "erp编码")
    private String erpGoodsInfoNo;

    /**
     * 仓库Id
     */
    @ApiModelProperty(value = "仓库Id")
    private Long wareId;
}
