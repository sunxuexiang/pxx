package com.wanmi.sbc.wms.api.response.pushWmsLogResponse;

import com.wanmi.sbc.wms.bean.vo.DescriptionFailedQueryStockPushVO;
import com.wanmi.sbc.wms.bean.vo.PushWmsLogVO;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 查询推送wms失败
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushWmsLogResponse implements Serializable {


    private static final long serialVersionUID = -3793075749674939332L;
    /**
     * 查询返回对象
     */
    private List<PushWmsLogVO> pushWmsLogVOS;
}
