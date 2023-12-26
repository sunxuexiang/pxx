package com.wanmi.sbc.logisticsbean.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author lm
 * @date 2022/11/04 9:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsBatchNoDTO implements Serializable {

    private static final long serialVersionUID = -8807674093765870168L;

    /**
     * 分类编号
     */
    private String goodsInfoId;

    /**
     * 货品的编号
     */
    private String goodsInfoBatchNo;

}
