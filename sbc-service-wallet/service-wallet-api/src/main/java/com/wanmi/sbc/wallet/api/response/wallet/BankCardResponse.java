package com.wanmi.sbc.wallet.api.response.wallet;

import com.wanmi.sbc.wallet.bean.vo.BindBankCardVo;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: 绑定银行卡响应类
 * @author: jiangxin
 * @create: 2021-08-20 15:23
 */
@Data
@ApiModel
@EqualsAndHashCode(callSuper = true)
public class BankCardResponse extends BindBankCardVo {

    private static final long serialVersionUID = -5841584961317063564L;
}
