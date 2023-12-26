package com.wanmi.sbc.wallet.api.provider.wallet;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "${application.wallet.name}", contextId = "WalletTicketsFormLogProvider")
public interface TicketsFormLogProvider {

}
