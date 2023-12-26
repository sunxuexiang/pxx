package com.wanmi.sbc.account.provider.impl.cashBack;

import com.wanmi.sbc.account.api.provider.cashBack.CashBackProvider;
import com.wanmi.sbc.account.api.request.cashBack.CashBackAddRequest;
import com.wanmi.sbc.account.api.request.cashBack.CashBackModifyRequest;
import com.wanmi.sbc.account.cashBack.model.root.CashBack;
import com.wanmi.sbc.account.cashBack.service.CashBackService;
import com.wanmi.sbc.common.base.BaseResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
@RestController
@Validated
public class CashBackController implements CashBackProvider {
    @Autowired
    private CashBackService cashBackService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse add(@RequestBody @Valid CashBackAddRequest request) {
        CashBack cashBack=new CashBack();
        BeanUtils.copyProperties(request, cashBack);
        cashBackService.add(cashBack);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse modify(@Valid CashBackModifyRequest request) {
        cashBackService.modify(request.getId(),request.getReturnStatus());
        return BaseResponse.SUCCESSFUL();
    }
}
