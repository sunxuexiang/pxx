package com.wanmi.sbc.goods.api.request.info;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * com.wanmi.sbc.goods.api.request.info.DistributionGoodsPageRequest
 * 分销商品分页请求对象
 *
 * @author chenjun
 * @dateTime 2020/5/25 上午9:33
 */
@ApiModel
@Data
public class SpecialGoodsModifyRequest extends BaseQueryRequest implements Serializable {

    private static final long serialVersionUID = 1976608100715393368L;
    /**
     * 折扣值
     */
    private String goodDiscount;

    private List<String> goodsInfoIdList;
//    private List<GoodsInfoVO> goodsInfoList;
}
