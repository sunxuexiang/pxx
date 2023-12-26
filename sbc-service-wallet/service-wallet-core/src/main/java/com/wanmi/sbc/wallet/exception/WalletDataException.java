
package com.wanmi.sbc.wallet.exception;

import lombok.Data;


@Data
public class WalletDataException {
  public String message; // optional
  public String callStack; // optional
  public String date; // optional
}

