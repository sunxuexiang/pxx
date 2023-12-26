package com.wanmi.sbc.wms.api.response.wms;

import com.wanmi.sbc.wms.bean.vo.InventoryQueryReturnVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: InventoryQueryResponse
 * @Description: TODO
 * @Author: yxb
 * @Date: 2020/5/7 17:43
 * @Version: 1.0
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryQueryResponse implements Serializable {


    private static final long serialVersionUID = -1600991349675684915L;
    /**
     * 已新增的请求记录信息
     */
    @ApiModelProperty(value = "已新增的请求记录信息")
    private List<InventoryQueryReturnVO> inventoryQueryReturnVO;

}
