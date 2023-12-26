package com.wanmi.sbc.live.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LiveGoodsVO implements Serializable {
    private static final long serialVersionUID = 8666702470267150944L;
    /**
     * skuid
     */
    private String goodsInfoId;
    /**
     * 0 批发 1散批
     */
    private Long goodsType;

    /**
     * 父级GoodsInfoId
     */
    private String parentGoodsInfoId;
}
