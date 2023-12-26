package com.wanmi.sbc.order.api.response.manualrefund;

import com.wanmi.sbc.order.bean.vo.RefundForClaimsApplyVO;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author chenchang
 * @since 2023/04/21 11:14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class RefundForClaimsApplyPageResponse implements Serializable {
    private List<RefundForClaimsApplyVO> list;

    private Integer currentPage;

    private Integer pageSize;

    private Long total;
}
