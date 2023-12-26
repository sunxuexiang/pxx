package com.wanmi.sbc.wms.api.request.pushWmsLog;

import com.wanmi.sbc.wms.api.request.WmsBaseRequest;
import com.wanmi.sbc.wms.api.request.wms.WMSChargeBackDetailsRequest;
import com.wanmi.sbc.wms.bean.vo.PushWmsLogVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;


@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushWmsLogRequest extends WmsBaseRequest {


    private static final long serialVersionUID = 6007708440751134411L;

    private PushWmsLogVO pushWmsLogVO;
}
