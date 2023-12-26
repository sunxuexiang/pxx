package com.wanmi.sbc.account.api.response.wallet;

import com.wanmi.sbc.account.bean.vo.BindBankCardVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: TODO
 * @author: jiangxin
 * @create: 2021-08-21 11:44
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BankCardInfoListResponse implements Serializable {

    private static final long serialVersionUID = -7702160152139923327L;

    /**
     * 银行卡信息列表
     */
    @ApiModelProperty(value = "银行卡信息列表")
    private List<BindBankCardVo> bindBankCardVoList;
}
