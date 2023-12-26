package com.wanmi.sbc.wallet.provider.impl.wallet;

import com.wanmi.sbc.wallet.api.provider.wallet.TicketsFormLogProvider;
import com.wanmi.sbc.wallet.wallet.service.TicketsFormLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class TicketsFormLogController implements TicketsFormLogProvider {

    @Autowired
    public TicketsFormLogService ticketsFormLogService;

}
