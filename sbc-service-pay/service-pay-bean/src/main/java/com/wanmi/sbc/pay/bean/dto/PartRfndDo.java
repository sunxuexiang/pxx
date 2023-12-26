package com.wanmi.sbc.pay.bean.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/6/17 16:03
 */
public class PartRfndDo implements Serializable {
    private static final long serialVersionUID = -3180371657103711950L;

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    private String Ittparty_Stm_Id ;
    private String Py_Chnl_Cd;
    private String Ittparty_Tms = formatter.format(new Date());
    private String Ittparty_Jrnl_No = System.currentTimeMillis() + "";
    private String Mkt_Id;
    private String Cust_Rfnd_Trcno;
    private String Py_Trn_No;
    private BigDecimal Rfnd_Amt;
    private List<PartRfndDoSubDo> Sub_Ordr_List;
    private String Vno;
    private String Sign_Inf;

    public String getIttparty_Stm_Id() {
        return Ittparty_Stm_Id;
    }

    public void setIttparty_Stm_Id(String ittparty_Stm_Id) {
        Ittparty_Stm_Id = ittparty_Stm_Id;
    }

    public String getPy_Chnl_Cd() {
        return Py_Chnl_Cd;
    }

    public void setPy_Chnl_Cd(String py_Chnl_Cd) {
        Py_Chnl_Cd = py_Chnl_Cd;
    }

    public String getIttparty_Tms() {
        return Ittparty_Tms;
    }

    public void setIttparty_Tms(String ittparty_Tms) {
        Ittparty_Tms = ittparty_Tms;
    }

    public String getIttparty_Jrnl_No() {
        return Ittparty_Jrnl_No;
    }

    public void setIttparty_Jrnl_No(String ittparty_Jrnl_No) {
        Ittparty_Jrnl_No = ittparty_Jrnl_No;
    }

    public String getMkt_Id() {
        return Mkt_Id;
    }

    public void setMkt_Id(String mkt_Id) {
        Mkt_Id = mkt_Id;
    }

    public String getPy_Trn_No() {
        return Py_Trn_No;
    }

    public void setPy_Trn_No(String py_Trn_No) {
        Py_Trn_No = py_Trn_No;
    }

    public BigDecimal getRfnd_Amt() {
        return Rfnd_Amt;
    }

    public void setRfnd_Amt(BigDecimal rfnd_Amt) {
        Rfnd_Amt = rfnd_Amt;
    }

    public List<PartRfndDoSubDo> getSub_Ordr_List() {
        return Sub_Ordr_List;
    }

    public void setSub_Ordr_List(List<PartRfndDoSubDo> sub_Ordr_List) {
        Sub_Ordr_List = sub_Ordr_List;
    }

    public String getVno() {
        return Vno;
    }

    public void setVno(String vno) {
        Vno = vno;
    }

    public String getSign_Inf() {
        return Sign_Inf;
    }

    public void setSign_Inf(String sign_Inf) {
        Sign_Inf = sign_Inf;
    }

    public String getCust_Rfnd_Trcno() {
        return Cust_Rfnd_Trcno;
    }

    public void setCust_Rfnd_Trcno(String cust_Rfnd_Trcno) {
        Cust_Rfnd_Trcno = cust_Rfnd_Trcno;
    }
}
