package com.wanmi.sbc.goods.api.response.stockoutmanage;

import com.wanmi.sbc.goods.bean.vo.StockoutManageVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>缺货管理修改结果</p>
 * @author tzx
 * @date 2020-05-27 11:37:26
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockoutManageModifyResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 已修改的缺货管理信息
     */
    @ApiModelProperty(value = "已修改的缺货管理信息")
    private StockoutManageVO stockoutManageVO;
}
