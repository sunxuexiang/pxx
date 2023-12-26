package com.wanmi.sbc.wallet.api.response.wallet;

import com.wanmi.sbc.wallet.bean.vo.VirtualGoodsVO;
import com.wanmi.sbc.common.base.MicroServicePage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author jeffrey
 * @create 2021-08-21 9:40
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VirtualGoodsResponse implements Serializable {

    private static final long serialVersionUID = -4659819275025148345L;

    @ApiModelProperty(value = "虚拟商品单个")
    private VirtualGoodsVO virtualGoods;

    @ApiModelProperty(value = "虚拟商品多个")
    private List<VirtualGoodsVO> virtualGoodsList;

    @ApiModelProperty(value ="分页查询")
    private MicroServicePage<VirtualGoodsVO> pageVirtualGoodsList;

}
