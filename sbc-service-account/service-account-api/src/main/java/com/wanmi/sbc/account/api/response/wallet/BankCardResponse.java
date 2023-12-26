package com.wanmi.sbc.account.api.response.wallet;

import com.wanmi.sbc.account.bean.vo.BindBankCardVo;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
