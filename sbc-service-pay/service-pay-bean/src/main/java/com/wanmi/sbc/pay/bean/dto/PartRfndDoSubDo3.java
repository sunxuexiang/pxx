package com.wanmi.sbc.pay.bean.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/6/17 16:08
 */
public class PartRfndDoSubDo3 implements Serializable {
    private static final long serialVersionUID = -146419466491913339L;
    private String Mkt_Mrch_Id;
    private BigDecimal Rfnd_Amt;

    public String getMkt_Mrch_Id() {
        return Mkt_Mrch_Id;
    }

    public void setMkt_Mrch_Id(String mkt_Mrch_Id) {
        Mkt_Mrch_Id = mkt_Mrch_Id;
    }

    public BigDecimal getRfnd_Amt() {
        return Rfnd_Amt;
    }

    public void setRfnd_Amt(BigDecimal rfnd_Amt) {
        Rfnd_Amt = rfnd_Amt;
    }
}
