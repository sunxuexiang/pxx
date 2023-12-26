package com.wanmi.sbc.pay.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/7/12 14:37
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CcbPayOrderResponse implements Serializable {

    private static final long serialVersionUID = -2834660920392481172L;

    private String Cshdk_Url;

    private String Main_Ordr_No;

    private String Prim_Ordr_No;

    private String Py_Trn_No;

    private String Rtn_Par_Data;

    private String Pay_Qr_Code;
}
