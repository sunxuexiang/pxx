package com.wanmi.sbc.account.api.request.finance.record;

import com.wanmi.sbc.account.bean.dto.SettlementDetailDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>新增结算明细列表request</p>
 * Created by of628-wenzhi on 2018-10-13-下午6:19.
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SettlementDetailListAddRequest extends AccountBaseRequest{
    private static final long serialVersionUID = -3808892991313230456L;

    /**
     * 结算明细列表
     */
    @ApiModelProperty(value = "结算明细列表")
    private List<SettlementDetailDTO> settlementDetailDTOList;
}
