package com.wanmi.sbc.goods.info.request;

import com.wanmi.sbc.common.base.BaseRequest;
import lombok.*;

/**
 * @ClassName: GoodsUpdateRequest
 * @Description: TODO
 * @Date: 2020/12/6 2:32
 * @Version: 1.0
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsUpdateRequest  {
    private Integer addedFlag;

    private Long brandId;

    private Integer saleType;

    private Long cateId;
}
