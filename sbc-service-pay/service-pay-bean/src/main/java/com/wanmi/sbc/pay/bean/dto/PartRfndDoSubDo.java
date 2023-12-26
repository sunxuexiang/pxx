package com.wanmi.sbc.pay.bean.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/6/17 16:04
 */
public class PartRfndDoSubDo implements Serializable {
    private static final long serialVersionUID = -1869668584470521782L;
    private String Sub_Ordr_Id;
    private BigDecimal Rfnd_Amt;
    private List<PartRfndDoSubDo3> Parlist;

    public List<PartRfndDoSubDo3> getParlist() {
        return Parlist;
    }

    public void setParlist(List<PartRfndDoSubDo3> parlist) {
        Parlist = parlist;
    }

    public String getSub_Ordr_Id() {
        return Sub_Ordr_Id;
    }

    public void setSub_Ordr_Id(String sub_Ordr_Id) {
        Sub_Ordr_Id = sub_Ordr_Id;
    }

    public BigDecimal getRfnd_Amt() {
        return Rfnd_Amt;
    }

    public void setRfnd_Amt(BigDecimal rfnd_Amt) {
        Rfnd_Amt = rfnd_Amt;
    }
}
