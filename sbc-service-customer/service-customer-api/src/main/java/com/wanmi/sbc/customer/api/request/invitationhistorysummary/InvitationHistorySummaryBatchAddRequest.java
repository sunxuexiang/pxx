package com.wanmi.sbc.customer.api.request.invitationhistorysummary;

import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.customer.bean.vo.InvitationHistorySummaryVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Max;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>邀新历史汇总计表新增参数</p>
 * @author fcq
 * @date 2021-04-27 11:31:55
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationHistorySummaryBatchAddRequest extends BaseRequest {

	List<InvitationHistorySummaryVO> historySummaryVO;
}