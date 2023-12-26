package com.wanmi.sbc.pay.api.request;

import com.wanmi.sbc.common.util.DateUtil;
import lombok.Data;

@Data
public class CmbReqDataRequest {

      private String dateTime;

      private String txCode;

      private String branchNo;

      private String merchantNo;
}
