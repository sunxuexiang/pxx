package com.wanmi.sbc.wms.api.response.erp;

import com.wanmi.sbc.wms.bean.vo.DescriptionFailedQueryStockPushVO;
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
public class DescriptionFailedQueryStockPushResponse implements Serializable {
    private static final long serialVersionUID = -4316592164853432580L;

    /**
     * 查询返回对象
     */
    private List<DescriptionFailedQueryStockPushVO> stockPushVO;
}
