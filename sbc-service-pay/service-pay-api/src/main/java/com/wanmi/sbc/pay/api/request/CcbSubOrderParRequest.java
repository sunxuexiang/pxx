package com.wanmi.sbc.pay.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/6/17 11:21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CcbSubOrderParRequest implements Serializable {

    private static final long serialVersionUID = 8880645452015390995L;
    /**
     * 顺序号
     */
    private Integer seqNo;

    /**
     * 商检编码
     */
    private String mktMrchId;

    /**
     * 金额 指定分账规则不用输入
     */
    private BigDecimal amt;
}
