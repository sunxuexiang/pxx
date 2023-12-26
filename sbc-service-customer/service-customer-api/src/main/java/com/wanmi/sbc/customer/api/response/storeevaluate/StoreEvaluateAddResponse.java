package com.wanmi.sbc.customer.api.response.storeevaluate;

import com.wanmi.sbc.customer.bean.vo.StoreEvaluateVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>店铺评价新增结果</p>
 * @author liutao
 * @date 2019-02-26 10:23:32
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreEvaluateAddResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 已新增的店铺评价信息
     */
    private StoreEvaluateVO storeEvaluateVO;
}
