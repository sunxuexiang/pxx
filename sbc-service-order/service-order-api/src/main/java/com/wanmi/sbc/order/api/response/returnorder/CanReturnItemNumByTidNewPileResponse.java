package com.wanmi.sbc.order.api.response.returnorder;

import com.wanmi.sbc.order.bean.vo.NewPileTradeVO;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel
public class CanReturnItemNumByTidNewPileResponse extends NewPileTradeVO {

    private static final long serialVersionUID = 4079600894275807085L;

}
