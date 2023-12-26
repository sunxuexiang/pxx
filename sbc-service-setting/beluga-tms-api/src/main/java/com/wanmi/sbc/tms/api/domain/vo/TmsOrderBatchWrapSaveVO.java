package com.wanmi.sbc.tms.api.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-09-19 10:11
 **/
@Data
public class TmsOrderBatchWrapSaveVO implements Serializable {

    private static final long serialVersionUID = 2245519248790629015L;

    /**
    * 承运单信息
    */
    private List<TmsOrderBatchSaveVO> orders;
}
