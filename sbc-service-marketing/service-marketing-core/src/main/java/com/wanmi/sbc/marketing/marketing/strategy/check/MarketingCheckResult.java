package com.wanmi.sbc.marketing.marketing.strategy.check;

import com.wanmi.sbc.common.util.CommonErrorCode;

public class MarketingCheckResult {

    private  Boolean success;

    private  String showMessage;

    private String resultCode;

    public MarketingCheckResult(){
        resultCode= CommonErrorCode.PARAMETER_ERROR;
        success = false;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getShowMessage() {
        return showMessage;
    }

    public void setShowMessage(String showMessage) {
        this.showMessage = showMessage;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public boolean isSuccess(){
        return success;
    }

}
