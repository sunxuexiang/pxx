package com.wanmi.sbc.wms.api.response.erp;

import com.wanmi.sbc.wms.bean.vo.DescriptionFailedQueryStockPushReturnGoodsVO;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 查询囤货推erp失败
 *
 * @author yitang
 * @version 1.0
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DescriptionFailedQueryStockPushReturnGoodsResponse implements Serializable {
    private static final long serialVersionUID = 147264054688097769L;

    private List<DescriptionFailedQueryStockPushReturnGoodsVO> returnGoodsVOList;
}
