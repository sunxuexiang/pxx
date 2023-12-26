package com.wanmi.sbc.goods.api.response.freight;

import com.wanmi.sbc.goods.bean.vo.FreightTemplateDeliveryAreaVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>根据id查询任意（包含已删除）配送到家范围信息response</p>
 * @author zhaowei
 * @date 2021-03-25 16:57:57
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreightTemplateDeliveryAreaByStoreIdResponse implements Serializable {

    private static final long serialVersionUID = -6543186068605631360L;

    /**
     * （常规）配送到家范围信息
     */
    @ApiModelProperty(value = "（常规）配送到家范围信息")
    private FreightTemplateDeliveryAreaVO freightTemplateDeliveryAreaVO;

    /**
     * （乡镇满十件）配送到家范围信息
     */
    @ApiModelProperty(value = "（乡镇满十件）配送到家范围信息")
    private FreightTemplateDeliveryAreaVO areaTenFreightTemplateDeliveryAreaVO;
}
