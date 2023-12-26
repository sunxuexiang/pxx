package com.wanmi.sbc.account.api.response.wallet;

import com.wanmi.sbc.account.bean.vo.BindBankCardVo;
import com.wanmi.sbc.common.base.MicroServicePage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description: 银行卡分页结果
 * @author: jiangxin
 * @create: 2021-08-23 11:29
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankCardPageResponse implements Serializable {

    private static final long serialVersionUID = -8486827135143956278L;

    /**
     * 银行卡信息分页
     */
    @ApiModelProperty(value = "带分页的银行卡信息")
    private MicroServicePage<BindBankCardVo> bindBankCardVoPage;
}
