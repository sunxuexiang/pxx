package com.wanmi.sbc.goods.goodsdevanning.request;

import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.common.enums.CompanyType;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsDevanningQueryRequest extends BaseRequest {

    private static final long serialVersionUID = 6653854465788543531L;
    /**
     * 批量商品编号
     */
    private List<String> goodsIds;

}