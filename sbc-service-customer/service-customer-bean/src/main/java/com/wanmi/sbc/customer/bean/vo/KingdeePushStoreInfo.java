package com.wanmi.sbc.customer.bean.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 描述
 *
 * @author yitang
 * @version 1.0
 * @date 2021/07/29 16:23:11
 */
@Setter
@Getter
public class KingdeePushStoreInfo implements Serializable {

    private static final long serialVersionUID = 5733006080881008443L;


    @JSONField(name = "FNumber")
    private String FNumber;


    @JSONField(name = "FName")
    private String FName;

    @JSONField(name = "F_ora_Remark")
    private String F_ora_Remark;

    @JSONField(name = "F_ora_Text")
    private String F_ora_Text;

    @JSONField(name = "FDescription")
    private String FDescription;

}
