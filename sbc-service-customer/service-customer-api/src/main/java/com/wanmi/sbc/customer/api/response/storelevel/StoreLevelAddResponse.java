package com.wanmi.sbc.customer.api.response.storelevel;

import com.wanmi.sbc.customer.bean.vo.StoreLevelVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>商户客户等级表新增结果</p>
 * @author yang
 * @date 2019-02-27 19:51:30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreLevelAddResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 已新增的商户客户等级表信息
     */
    private StoreLevelVO storeLevelVO;
}
