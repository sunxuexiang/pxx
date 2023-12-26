package com.wanmi.sbc.returnorder.api.response.groupon;

import com.wanmi.sbc.returnorder.bean.vo.GrouponDetailVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>团明细</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrouponDetailQueryResponse implements Serializable {
    private static final long serialVersionUID = -9076705454351078810L;

    /***
     * 团明细
     */
    private GrouponDetailVO grouponDetail;



}
