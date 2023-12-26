package com.wanmi.sbc.shopcart.api.request.purchase;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.shopcart.bean.vo.PilePurchaseActionVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description: 囤货明细查询条件请求类
 * @author: XinJiang
 * @time: 2021/12/20 16:21
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PilePurchaseActionRequestVO extends BaseQueryRequest {

    private static final long serialVersionUID = 5813508226744652612L;

    /**
     * 会员编号
     */
    private String customerId;

    /**
     * SKU编号
     */
    private String goodsInfoId;
    /**
     * 批量SKU编号
     */
    private List<String> goodsInfoIds;
    /**
     * 批量sku
     */
    private List<GoodsInfoDTO> goodsInfos;

    /**
     * 是否后台同步标识
     */
    private Boolean syncFlag;

    /**
     * 囤货明细信息 保存时使用
     */
    private List<PilePurchaseActionVO> purchaseActionVOList;

}
