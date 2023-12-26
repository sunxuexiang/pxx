package com.wanmi.sbc.setting.api.response.retaildeliveryconfig;

import com.wanmi.sbc.setting.bean.vo.PackingConfigVO;
import com.wanmi.sbc.setting.bean.vo.RetailDeliverConfigVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>onlineService新增结果</p>
 * @author lq
 * @date 2019-11-05 16:10:28
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetailDeliveryConfigResponse implements Serializable {


    private static final long serialVersionUID = -8430106998825079173L;
    @ApiModelProperty(value = "包装配置信息")
    private RetailDeliverConfigVO retailDeliverConfigVO;
}
